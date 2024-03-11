package com.example.boardgame;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.boardgame.utility.FileUtils;
import com.example.boardgame.utility.JsonToData;
import com.example.boardgame.vo.meetingVO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

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

public class updateMeeting extends AppCompatActivity {

    private ImageView chooseImg;
    private Button inputMeeting;
    private Button toChooseCafe;
    private EditText inputSearch;
    private EditText memberNum;
    private EditText meetingName;
    private EditText meetingContent;
    private TextView cafeName;
    private TextView cafeAddress;
    private String change = "1"; // 이미지가 변경되었는지 확인하는변수 1 이면 변경이 안됨 2면 변경됨
    private int SetInputMode = 1; //이미지를 어떤 형식으로 저장해야하나 카메라에서 사진 찍어서 가져왔으면 2 갤리리에서 가져왔으면 3
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 125;
    private final int MY_PERMISSIONS_REQUEST_GALLERY = 124;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    meetingVO vo = new meetingVO();
    Bitmap photo; // 카메라로 찍은 이미지 비트맵
    Uri selectedImageUri; // 갤러리에서 가져온 uri
    String placeName;
    String roadAddressName;
    String x;
    String y;
    String updateX;
    String updateY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_meeting);

        // ui 연결
        chooseImg = findViewById(R.id.chooseImg);
        inputMeeting = findViewById(R.id.inputMeeting);
        toChooseCafe = findViewById(R.id.toChooseCafe);
        inputSearch = findViewById(R.id.inputSearch);
        memberNum = findViewById(R.id.memberNum);
        meetingName = findViewById(R.id.meetingName);
        meetingContent = findViewById(R.id.meetingContent);
        cafeName = findViewById(R.id.cafeName);
        cafeAddress = findViewById(R.id.cafeAddress);

        // 인텐트를 정보를 가져옴
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);

        getMeeting(id);

        placeName = intent.getStringExtra("placeName");
        roadAddressName = intent.getStringExtra("roadAddressName");
        updateX = intent.getStringExtra("x");
        updateY = intent.getStringExtra("y");

        System.out.println("수정한 x 좌표 : " + x);
        System.out.println("수정한 y 좌표 : " + y);

        inputMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(placeName != null){
                    System.out.println("최종 updateX 좌표 : " + updateX);
                    System.out.println("최종 updateY 좌표 : " + updateY);
                } else if (placeName == null) {
                    System.out.println("최종 x 좌표 : " + x);
                    System.out.println("최종 y 좌표 : " + y);
                }

                if(SetInputMode == 1){ // 기본 이미지
                    noImg noImg = new noImg();
                    noImg.uploadNOImage(id);
                } else if (SetInputMode == 2) { // 카메라 이미지
                    BitmapUpload bitmapUpload = new BitmapUpload();
                    bitmapUpload.uploadImage(photo, id);
                } else if (SetInputMode == 3) { // 갤러리 이미지
                    UriUpload uriUpload = new UriUpload(getApplicationContext());
                    uriUpload.uploadImage(selectedImageUri, id);
                }

                System.out.println("카페 이름 : " + cafeName.getText().toString());

            }
        });

        // ActivityResultLauncher 초기화
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            photo = (Bitmap) data.getExtras().get("data");
                            chooseImg.setImageBitmap(photo);
                            SetInputMode = 2;
                        }
                    }
                });
        // ActivityResultLauncher 초기화
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            // 선택한 이미지를 이미지뷰에 표시
                            chooseImg.setImageURI(selectedImageUri);
                            // 이미지를 서버에 업로드하는 작업을 여기에 추가
                            SetInputMode = 3;
                        }
                    }
                });

        // 검색 입력칸이 하나라도 있어야 입력칸이 활성화
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String search = inputSearch.getText().toString();
                if (search.length() >= 1){
                    toChooseCafe.setEnabled(true);
                } else {
                    toChooseCafe.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 이미지 클릭시
        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseDialog(); // 이미지 뷰를 선택하면 다이얼로그가 나타남
            }
        });

        // 카페 검색 사이트로 이동
        toChooseCafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(updateMeeting.this, kakaoMap.class);
                String search = inputSearch.getText().toString();
                intent1.putExtra("search", search);
                intent1.putExtra("id", id);
                startActivity(intent1);
            }
        });
    } // end OnCreate

    private void getMeeting(int id){
        // 요청할 url 등록
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/meeting/getMeeting.php").newBuilder();
        urlBuilder.addQueryParameter("id", String.valueOf(id)); // url 쿼리에 id 라는 메개변수 추가
        String url = urlBuilder.build().toString(); // 최종 url 생성

        // Request 객체 생성
        Request request = new Request.Builder()
                .url(url)
                .build();

        // client 객체 생성
        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseData = response.body().string();
                    JsonToData jt = new JsonToData(); // 받아온 json을 vo객체에 담는 함수가 있는 클래스
                    vo = jt.jsonMeetingGet(responseData);

                    // UI 업데이트는 메인 스레드에서 실행해야 함
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // ui에 가져온 데이터를 설정
                            meetingName.setText(vo.getMeetingName()); // 모임의 이름을 설정함
                            meetingContent.setText(vo.getMeetingContent()); // 모임의 내용을 설정함
                            memberNum.setText(vo.getMeetingMembers()); // 모임의 최대 인원수 설정
                            x = vo.getMeetingLnt();
                            y = vo.getMeetingLat();
                            System.out.println("설정된 최대 인원수  : " + vo.getMeetingMembers());

                            if(placeName != null){
                                cafeName.setText(placeName);
                                cafeAddress.setText(roadAddressName);

                            } else if (placeName == null){
                                cafeName.setText(vo.getMeetingPlaceName());
                                cafeAddress.setText(vo.getMeetingAddress());
                                System.out.println("원래의 x 좌표 : " + x);
                                System.out.println("원래의 y 좌표 : " + y);
                            }
                            if (vo.getMeetingUrl() != null && !vo.getMeetingUrl().equals("null") && !vo.getMeetingUrl().equals("")) {
                                Glide.with(getApplicationContext()).load("http://3.38.213.196" + vo.getMeetingUrl()).into(chooseImg);
                            } else {
                                chooseImg.setImageResource(R.drawable.img);
                            }

                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "데이터를 가져오는데 실패했습니다 : " + response.body().string(), Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(getApplicationContext(), "데이터를 가져오는데 실패했습니다 : " + call.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    } // end getMeeting

    void showChooseDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this); // 다이얼로그 객체 생성
        alertDialogBuilder.setTitle("알림"); // 다이얼로그의 제목 설정

        alertDialogBuilder.setMessage("갤러리 이미지 기본 이미지 선택해주세요"); // 다이얼로그에 나올 메시지 설정

        // 다이얼로그중 카메라리를 선택했을때 실행되는 이벤트
        alertDialogBuilder.setPositiveButton("카메라", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                change = "2";
                checkCameraPermission();
            }
        });

        // 다이얼로그중 갤러리를 선택햇을때 실행되는 이벤트
        alertDialogBuilder.setNegativeButton("갤러리", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                change = "2";
                checkGalleryPermission();
            }
        });

        alertDialogBuilder.setNeutralButton("기본 이미지", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                change = "2";
                chooseImg.setImageResource(R.drawable.img);
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
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(cameraIntent);
    }
    // 갤러리 실행 함수
    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private class noImg{

        String serverUrl = "http://3.38.213.196/meeting/updateMeeting.php";

        public void uploadNOImage(int id){
            if(placeName != null){
                x = updateX;
                y = updateY;
            }

            OkHttpClient client = new OkHttpClient();

            RequestBody body = new FormBody.Builder()
                    .add("meeting_seq", String.valueOf(id))
                    .add("meeting_name",meetingName.getText().toString())
                    .add("meeting_content", meetingContent.getText().toString())
                    .add("meeting_members", memberNum.getText().toString())
                    .add("meeting_address", cafeAddress.getText().toString())
                    .add("meeting_place_name", cafeName.getText().toString())
                    .add("x", x)
                    .add("y", y)
                    .add("change", change)
                    .build();

            Request request = new Request.Builder()
                    .url(serverUrl)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String responseBodyString = response.body().string();
                                Toast.makeText(getApplicationContext(), "수정이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    });
                    // 수정이 완료되면 수정된 페이지로 이동
                    Intent intent = new Intent(updateMeeting.this, getMeeting.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }
            });

        }
    }

    private class UriUpload{
        private Context context;

        private String serverUrl = "http://3.38.213.196/meeting/updateMeeting.php";

        public UriUpload(Context context){
            this.context = context;
        }

        public void uploadImage(Uri uri, int id){
            if(uri == null){
                // 이미지가 없을 경우 처리
                return;
            }

            if(placeName != null){
                x = updateX;
                y = updateY;
            }

            OkHttpClient client = new OkHttpClient();

            // uri에서 실제 파일 경로 가져오기
            String filePath = FileUtils.getPathFromUri(context, uri);
            File file = new File(filePath);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", "image.png", RequestBody.create(MediaType.parse("image/*"), file))
                    .addFormDataPart("meeting_seq", String.valueOf(id))
                    .addFormDataPart("meeting_name",meetingName.getText().toString())
                    .addFormDataPart("meeting_content", meetingContent.getText().toString())
                    .addFormDataPart("meeting_members", memberNum.getText().toString())
                    .addFormDataPart("meeting_address", cafeAddress.getText().toString())
                    .addFormDataPart("meeting_place_name", cafeName.getText().toString())
                    .addFormDataPart("x", x)
                    .addFormDataPart("y", y)
                    .addFormDataPart("change", change)
                    .build();

            Request request = new Request.Builder()
                    .url(serverUrl)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String responseBodyString = response.body().string();
                                Toast.makeText(getApplicationContext(), "수정이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    });
                    // 수정이 완료되면 수정된 페이지로 이동
                    Intent intent = new Intent(updateMeeting.this, getMeeting.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), call.toString(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });
        }

    }

    private class BitmapUpload{
        public void uploadImage(Bitmap bitmap, int id){

            if(placeName != null){
                x = updateX;
                y = updateY;
            }
            if(bitmap == null){
                return;
            }

            // 이미지를 ByteArray로 변환
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            // 서버의 URL 설정
            String serverUrl = "http://3.38.213.196/meeting/updateMeeting.php";
            // OkHttpClient 객체 생성
            OkHttpClient client = new OkHttpClient();

            // RequestBody 생성
            MediaType mediaType = MediaType.parse("image/*");

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", "image.png", RequestBody.create(byteArray, mediaType))
                    .addFormDataPart("meeting_seq", String.valueOf(id))
                    .addFormDataPart("meeting_name",meetingName.getText().toString())
                    .addFormDataPart("meeting_content", meetingContent.getText().toString())
                    .addFormDataPart("meeting_members", memberNum.getText().toString())
                    .addFormDataPart("meeting_address", cafeAddress.getText().toString())
                    .addFormDataPart("meeting_place_name", cafeName.getText().toString())
                    .addFormDataPart("x", x)
                    .addFormDataPart("y", y)
                    .addFormDataPart("change", change)
                    .build();

            Request request = new Request.Builder()
                    .url(serverUrl)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String responseBodyString = response.body().string();
                                Toast.makeText(getApplicationContext(), "수정이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    });
                    // 수정이 완료되면 수정된 페이지로 이동
                    Intent intent = new Intent(updateMeeting.this, getMeeting.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Toast.makeText(updateMeeting.this, "수정을 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }
}