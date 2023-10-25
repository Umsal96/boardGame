package com.example.boardgame;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.example.boardgame.utility.LocalDialog;
import com.example.boardgame.vo.locationVO;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class kakaoMap extends AppCompatActivity implements OnMapReadyCallback {

    ArrayList<locationVO> voArrayList = new ArrayList<>();

    private NaverMap naverMap;
    private MapView mapView;
    int id;
    String where;
    String num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao_map);

        Intent intent = getIntent();
        String search = intent.getStringExtra("search");
        id = intent.getIntExtra("id", 0);
        where = intent.getStringExtra("where");
        num = intent.getStringExtra("num");
        new KakaoDataAsyncTask(search).execute();

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
    }
    private void createMarkers(){

        for (int i = 0; i < voArrayList.size(); i++) {
            Marker[] markers = new Marker[voArrayList.size()];
            markers[i] = new Marker();

            String slat = voArrayList.get(i).getY();
            String slnt = voArrayList.get(i).getX();

            double lat = Double.parseDouble(slat);
            double lnt = Double.parseDouble(slnt);
            markers[i].setCaptionText(voArrayList.get(i).getPlace_name());
            markers[i].setPosition(new LatLng(lat, lnt));
            markers[i].setMap(naverMap);

            int finalI = i;
            markers[i].setOnClickListener(new Overlay.OnClickListener() {
                @Override
                public boolean onClick(@NonNull Overlay overlay) {
                    LocalDialog localDialog = new LocalDialog(kakaoMap.this, kakaoMap.this ,voArrayList.get(finalI).getPlace_name(),
                            voArrayList.get(finalI).getX(), voArrayList.get(finalI).getY(), voArrayList.get(finalI).getRoad_address_name(), id, where, num);
                    localDialog.show();
                    return false;
                }
            });

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    // 노원 보드게임 카페 정보 통신 이벤트
    private class KakaoDataAsyncTask extends AsyncTask<Void, Void, String>{

        String search;
        public KakaoDataAsyncTask(String search) {
            this.search = search;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                OkHttpClient client = new OkHttpClient();
                String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query="+ search +"보드게임카페&size=15";

                // 요청 생성
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "KakaoAK 160539041b3b240d176a2942ad7b36dc")
                        .build();

                // 요청 보내기
                Response response = client.newCall(request).execute();

                if(response.isSuccessful()){
                    // 응답 성공 시 JSON 데이터 추출
                    String jsonData = response.body().string();
                    return jsonData;
                } else {
                    Log.e(TAG, "HTTP 요청 실패: " + response.code());
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String jsonData){
            if(jsonData != null){
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONArray documentsArray = jsonObject.getJSONArray("documents");

                    // documentsArray를 반복하면서 각 항목을 처리합니다.
                    for (int i = 0; i < documentsArray.length(); i++) {
                        JSONObject document = documentsArray.getJSONObject(i);
                        String subCategory = document.getString("category_name");
                        String sub = "카페";
                        if(subCategory.contains(sub)){
                            // document에서 필요한 정보 추출
                            String placeName = document.getString("place_name");
                            String placeUrl = document.getString("place_url");
                            String categoryName = document.getString("category_name");
                            String addressName = document.getString("address_name");
                            String roadAddressName = document.getString("road_address_name");
                            String id = document.getString("id");
                            String phone = document.getString("phone");
                            String categoryGroupCode = document.getString("category_group_code");
                            String categoryGroupName = document.getString("category_group_name");
                            String x = document.getString("x");
                            String y = document.getString("y");

                            voArrayList.add(new locationVO(placeName, placeUrl, categoryName, addressName, roadAddressName,
                                    id, phone, categoryGroupCode, categoryGroupName, x, y));
                        }
                    }
                    if(voArrayList.size() > 0 && naverMap != null){
                        double lat = Double.parseDouble(voArrayList.get(0).getY());
                        double lnt = Double.parseDouble(voArrayList.get(0).getX());
                        naverMap.setCameraPosition(new CameraPosition(
                                new LatLng(lat, lnt),
                                13
                        ));
                        createMarkers();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "요청 실패 또는 예외 발생");
            }

            if (naverMap != null) {
                createMarkers();
            }
        }
    }
}