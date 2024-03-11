package com.example.boardgame;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.boardgame.Adapter.ImageViewPagerAdapter;
import com.example.boardgame.utility.FileUtils;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class inputCafe extends AppCompatActivity implements OnMapReadyCallback {
    private NaverMap naverMap;
    private MapView mapView;
    private ImageButton backPage; // 뒤로 가기 버튼
    private ImageButton inputImageButton; // 이미지 입력 버튼
    private Button inputCafe; // 카페 정보 입력 버튼
    private Button inputCafeSearchButton; // 카페 장소 검색 버튼
    private EditText inputCafeSearchBox; // 카페 검색어 칸
    private EditText cafeDetail; // 카페 정보 입력칸
    private TextView cafeName; // 카페 이름 정보 칸
    private TextView cafeAddress; // 카페 주소 칸
    private ViewPager2 cafeInputViewPager; // 카페 이미지 뷰 페이져
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 125;
    private final int MY_PERMISSIONS_REQUEST_GALLERY = 124;
    private ArrayList<Uri> imageDataList = new ArrayList<>();
    private String currentPhotoPath;
    private ImageViewPagerAdapter imageViewPagerAdapter;
    String sx;
    String sy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_cafe);

        backPage = findViewById(R.id.backPage);
        inputImageButton = findViewById(R.id.inputImageButton);
        inputCafe = findViewById(R.id.inputCafe);
        inputCafeSearchButton = findViewById(R.id.inputCafeSearchButton);
        inputCafeSearchBox = findViewById(R.id.inputCafeSearchBox);
        cafeDetail = findViewById(R.id.cafeDetail);
        cafeName = findViewById(R.id.cafeName);
        cafeAddress = findViewById(R.id.cafeAddress);
        cafeInputViewPager = findViewById(R.id.cafeInputViewPager);
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        imageViewPagerAdapter = new ImageViewPagerAdapter(imageDataList);
        cafeInputViewPager.setAdapter(imageViewPagerAdapter);

        imageViewPagerAdapter.setOnCancelClickListener(new ImageViewPagerAdapter.OnCancelClickListener() {
            @Override
            public void onCancelClick(int position) {
                imageDataList.remove(position);
                imageViewPagerAdapter.notifyItemRemoved(position);
            }
        });

        inputCafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputCafeInfo(); // 카페 정보 입력
            }
        });

        // 카페 위치 정보
        inputCafeSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = inputCafeSearchBox.getText().toString();
                Intent intent = new Intent(inputCafe.this, kakaoMap.class);
                intent.putExtra("search", search);
                intent.putExtra("where", "cafe");
                startActivity(intent);
            }
        });
        // 이미지 입력
        inputImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미지 어떻게 입력할건지 선택하는 다이얼로그
                showChooseImgDialog();
            }
        });

        // ActivityResultLauncher 초기화
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Uri photoUri = Uri.fromFile(new File(currentPhotoPath));
                        System.out.println("Uri : " + photoUri);
                        imageDataList.add(photoUri);
                        imageViewPagerAdapter.notifyDataSetChanged();
                    }
                });
        // ActivityResultLauncher 초기화
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK && result.getData() != null){
                        ClipData clipData = result.getData().getClipData();
                        if(clipData != null){
                            // 여러 이미지를 선택한 경우
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri imageUri = clipData.getItemAt(i).getUri();
                                imageDataList.add(imageUri);
                                System.out.println(imageUri.toString());
                                System.out.println("여러이미지");
                            }
                            imageViewPagerAdapter.notifyDataSetChanged();
                            System.out.println("최대 길이 : " + imageDataList.size());

                        }else {
                            // 하난의 이미지를 선택한 경우
                            Uri imageUri = result.getData().getData();
                            imageDataList.add(imageUri);
                        }
                    }
                });
    }

    // 게임 정보 입력
    private void inputCafeInfo(){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/cafe/inputCafe.php").newBuilder();
        String url = urlBuilder.build().toString();

        RequestBody requestBody;

        if(imageDataList.size() > 0){
            // 이미지가 있을 경우
            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("cafeName", cafeName.getText().toString())
                    .addFormDataPart("cafeLat", sy)
                    .addFormDataPart("cafeLnt", sx)
                    .addFormDataPart("cafeAddress", cafeAddress.getText().toString())
                    .addFormDataPart("cafeContent", cafeDetail.getText().toString());

            // 각 이미지를 별도의 파트로 추가
            for (int i = 0; i < imageDataList.size(); i++) {
                Uri imageUri = imageDataList.get(i);

                String imageUriString = imageUri.toString();

                String filePath;

                // imageUri가 'file://'로 시작하는 경우
                if (imageUriString.startsWith("file://")) {
                    // 'file://'을 제거하여 실제 파일 경로를 얻음
                    filePath = imageUriString.substring(7);
                } else {
                    filePath = FileUtils.getPathFromUri(getApplicationContext(), imageUri);
                }

                File file = new File(filePath);
                multipartBuilder.addFormDataPart("image" + i, "image"+i+".png", RequestBody.create(MediaType.parse("image/*"), file));
            }

            requestBody = multipartBuilder.build();
        }else {
            // 이미지가 없을경우

            // POST 요청 본문을 생성
            requestBody = new FormBody.Builder()
                    .add("cafeName", cafeName.getText().toString())
                    .add("cafeLat", sy)
                    .add("cafeLnt", sx)
                    .add("cafeAddress", cafeAddress.getText().toString())
                    .add("cafeContent", cafeDetail.getText().toString())
                    .build();
        }

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseData = response.body().string();

                    System.out.println(responseData);

                    Intent intent = new Intent(inputCafe.this, main.class);
                    intent.putExtra("where", 4);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

        Intent intent1 = getIntent();
        String cafeName1 = intent1.getStringExtra("cafeName");
        String cafeAddress1 = intent1.getStringExtra("cafeAddress");
        String x = intent1.getStringExtra("x");
        String y = intent1.getStringExtra("y");
        System.out.println("cafeName : " + cafeName1);
        System.out.println("cafeAddress : " + cafeAddress1);
        System.out.println("x : " + x);
        System.out.println("y : " + y);

        if(cafeName1 != null){
            //카페 위치 정보 를 받아왔을때 실행
            setMarker(x, y, cafeName1);
            sx = x;
            sy = y;
            cafeName.setText(cafeName1);
            cafeAddress.setText(cafeAddress1);
        }
    }

    // 갤러리 카메라 선택할수 있는 다이얼로그 표시 함수
    private void showChooseImgDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this); // 다이얼로그 객체 생성
        alertDialogBuilder.setTitle("알림"); // 다이얼로그의 제목 설정

        alertDialogBuilder.setMessage("갤러리 카메라중 선택해주세요"); // 다이얼로그 나올 메시지 설멍
        // 다이얼로그 중 카메라를 선택했을때 실행되는 이벤트
        alertDialogBuilder.setPositiveButton("카메라", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkCameraPermission();
            }
        });
        // 다이얼로그 중 갤러리를 선택했을때 실행되는 이벤트
        alertDialogBuilder.setNegativeButton("갤러리", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkGalleryPermission();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    // 카메라 사용 권한 있는지 확인용 함수
    private void checkCameraPermission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            openCamera();
        }
    }

    // 갤러리 이미지 사용 권한 확인용 함수
    private void checkGalleryPermission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    MY_PERMISSIONS_REQUEST_GALLERY);
        } else {
            openGallery();
        }
    }

    // 카메라 실행 함수
    private void openCamera(){
        System.out.println("카메라 실행");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        System.out.println("카메라 실행 2222");
        File photoFile = null;
        try{
            photoFile = createImageFile();
        }catch (IOException ex){

        }
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.boardgame.fileprovider",
                    photoFile);
            System.out.println("Uri : " + photoURI);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            cameraLauncher.launch(cameraIntent);
        }
    }

    // 갤러리 실행 함수
    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void setMarker(String x, String y, String cafeName){
        Marker marker = new Marker();
        double lat = Double.parseDouble(y);
        double lnt = Double.parseDouble(x);

        marker.setCaptionText(cafeName);
        marker.setPosition(new LatLng(lat, lnt));
        marker.setMap(naverMap);

        naverMap.setCameraPosition(new CameraPosition(
                new LatLng(lat, lnt),
                16
        ));
    }
}