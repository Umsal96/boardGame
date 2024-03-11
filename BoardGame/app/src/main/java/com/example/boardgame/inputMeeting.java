package com.example.boardgame;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import com.example.boardgame.utility.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class inputMeeting extends AppCompatActivity {

    private EditText memberNum; // 정원 설정
    private ImageView chooseImg; // 이미지가 출력될 곳
    private Button toChooseCafe; // 카페 선택용 페이지 이동용 버튼
    private EditText inputSearch; // 주소 검색어 입력용
    private TextView cafeName; // 장소 이름
    private TextView cafeAddress; // 장소 도로명 주소
    private Button inputMeeting; // 모임을 만들기 위한 버튼
    private EditText meetingName; // 모임의 이름 입력
    private EditText meetingContent; // 모임의 내용 입력
    private int SetInputMode = 1; // 이미지를 어떤 형식으로 저장해야하나 카메라에서 사진 찍어서 가져왔으면 2 갤리리에서 가져왔으면 3
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 125;
    private final int MY_PERMISSIONS_REQUEST_GALLERY = 124;
    String id; // 유저의 고유 아이디
    String sNum; // 모임이 설정된 인원수
    Bitmap photo; // 카메라로 찍은 이미지 비트맵

    Uri selectedImageUri;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;

    String placeName;
    String roadAddressName;
    String x;
    String y;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_meeting);

        // ui 연결
        toChooseCafe = findViewById(R.id.toChooseCafe); // 카페 선택 페이지 이동용 버튼
        inputSearch = findViewById(R.id.inputSearch); // 주소 검색 입력칸
        cafeName = findViewById(R.id.cafeName); // 인텐트로 받아온 카페 이름
        cafeAddress = findViewById(R.id.cafeAddress); // 인텐트로 받아온 카페 주소
        chooseImg = findViewById(R.id.chooseImg); // 이미지 뷰
        inputMeeting = findViewById(R.id.inputMeeting); // 미팅 입력 버튼
        memberNum = findViewById(R.id.memberNum); // 인원수 입력
        meetingName = findViewById(R.id.meetingName); // 모임 이름 입력
        meetingContent = findViewById(R.id.meetingContent); // 모임 내용 입력

        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        id = sharedPreferences.getString("userId", "");

        Intent intent = getIntent(); // 인텐트를 받음
        placeName = intent.getStringExtra("placeName");
        roadAddressName = intent.getStringExtra("roadAddressName");
        x = intent.getStringExtra("x");
        y = intent.getStringExtra("y");
        if(placeName != null){
            System.out.println("placeName : " + placeName);
            System.out.println("readAddressName : " + roadAddressName);
            System.out.println("x 좌표는? : " + x);
            System.out.println("y 좌표는? : " + y);
            double lat = Double.parseDouble(y);
            double lnt = Double.parseDouble(x);

            cafeName.setText(placeName);
            cafeAddress.setText(roadAddressName);
        }

        inputMeeting.setOnClickListener(new View.OnClickListener() { // 페이지 입력 버튼을 눌렀을때
            @Override
            public void onClick(View v) {
                sNum = memberNum.getText().toString(); // 인원수를 받음

                String name = meetingName.getText().toString(); // 모임 이름을 받아옴
                String content = meetingContent.getText().toString(); // 모임의 내용을 받아옴

                int nameLength = name.length();
                int contentLength = content.length();

                if(nameLength < 1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(inputMeeting.this);
                    builder.setMessage("모임 이름을 설정해 주세요")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    meetingName.requestFocus();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else if(contentLength < 5){
                    AlertDialog.Builder builder = new AlertDialog.Builder(inputMeeting.this);
                    builder.setMessage("모임 내용을 5글자 이상 입력해 주세요")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    meetingContent.requestFocus();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else if (sNum.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(inputMeeting.this);
                    builder.setMessage("인원수를 설정해주세요")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    memberNum.requestFocus();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    int Num = Integer.parseInt(sNum);
                    if(Num < 2 || Num > 100){
                        System.out.println("인원수를 조정해주세요");
                        AlertDialog.Builder builder = new AlertDialog.Builder(inputMeeting.this);
                        builder.setMessage("인원수를 조정해 주세요")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        memberNum.setText("");
                                        memberNum.requestFocus();
                                    }
                                });
                        // 다이얼로그 표시
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }

                if(SetInputMode == 1){
                    System.out.println("아무것도 없습니다.");
                    new Task().execute();
                } else if(SetInputMode == 2){
                    System.out.println("카메라로 찍었습니다.");
                    new cameraTask().execute(photo);
                } else if(SetInputMode == 3){
                    System.out.println("갤러리로 얻었습니다.");
                    System.out.println("uri : " + selectedImageUri);
                    new galleryTask(getApplicationContext()).execute(selectedImageUri);
                }
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

        toChooseCafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(inputMeeting.this, kakaoMap.class);
                String search = inputSearch.getText().toString();
                intent.putExtra("search", search);
                startActivity(intent);
            }
        });

//        try{
//            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures){
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("키해시는 :", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e){
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e){
//            e.printStackTrace();
//        }

        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("클릭되었습니다.");
                showChooseDialog(); // 이미지 뷰를 선택하면 다이얼로그가 나타남
            }
        });
    } // end onCreate

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                // 권한이 거부되었을 때 사용자에게 알림을 표시할 수 있습니다.
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("권한 필요");
                builder.setMessage("카메라 권한을 허용해야 카메라를 사용할 수 있습니다.");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        }

        if (requestCode == MY_PERMISSIONS_REQUEST_GALLERY) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            } else {
                // 갤러리 권한이 거부되었을 때 사용자에게 알림을 표시할 수 있습니다.
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("권한 필요");
                builder.setMessage("갤러리 권한을 허용해야 갤러리를 사용할 수 있습니다.");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }

        }
    }

    // 갤러리 이미지 기본 이미지 선택 할 수 있는 다이얼 로그 표시 함수
    void showChooseDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this); // 다이얼로그 객체 생성
        alertDialogBuilder.setTitle("알림"); // 다이얼로그의 제목 설정

        alertDialogBuilder.setMessage("갤러리 이미지 기본 이미지 선택해주세요"); // 다이얼로그에 나올 메시지 설정

        // 다이얼로그중 카메라리를 선택했을때 실행되는 이벤트
        alertDialogBuilder.setPositiveButton("카메라", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkCameraPermission();
            }
        });

        // 다이얼로그중 갤러리를 선택햇을때 실행되는 이벤트
        alertDialogBuilder.setNegativeButton("갤러리", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkGalleryPermission();
            }
        });

        alertDialogBuilder.setNeutralButton("기본 이미지", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chooseImg.setImageResource(R.drawable.photo_camera_24px);
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    // 카메라 사용 권한 있는지 확인용 함수
    private void checkCameraPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            openCamera();
        }
    }

    // 갤러리 이미지 사용 권한 확인용 함수
    private void checkGalleryPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
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

    // 카메라 이미지 업로드
    private class cameraTask extends AsyncTask<Bitmap, Void, String>{

        @Override
        protected String doInBackground(Bitmap... bitmaps) {

            if(bitmaps.length == 0 || bitmaps[0] == null){
                return "이미지가 없습니다.";
            }

            try{
                Bitmap bitmap = bitmaps[0];

                // 이미지를 ByteArray로 변환
                // ByteArrayOutputStream 생성 주로 데이터를 바이트 배열로 변환하거나, 데이터를 메모리에서 처리할 때 유용하게 사용됨
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                // 서버의 url 설정
                String serverUrl = "http://3.38.213.196/meeting/inputMeeting.php";

                // OkHttpClient 객체 생성
                OkHttpClient client = new OkHttpClient();

                // RequestBody 생성
                MediaType mediaType = MediaType.parse("image/*");
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", "image.png", RequestBody.create(byteArray, mediaType))
                        .addFormDataPart("user_id", id)
                        .addFormDataPart("x", x)
                        .addFormDataPart("y", y)
                        .addFormDataPart("name", meetingName.getText().toString())
                        .addFormDataPart("content", meetingContent.getText().toString())
                        .addFormDataPart("member", sNum)
                        .addFormDataPart("address", roadAddressName)
                        .addFormDataPart("placeName", placeName)
                        .build();

                Request request = new Request.Builder()
                        .url(serverUrl)
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                // 서버 응답 출력
                if(response.isSuccessful()){
                    return response.body().string(); // 서버 응답 반환
                } else {
                    return "이미지 업로드 실패";
                }
            } catch (IOException e){
                e.printStackTrace();
                return "오류 발생 : " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result){
            Toast.makeText(inputMeeting.this, result, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(inputMeeting.this, main.class);
            intent.putExtra("where", "1"); // 어디로 강제 이동할 것인지 설정
            startActivity(intent);
        }
    }
    // 갤러리 이미지 업로드
    private class galleryTask extends  AsyncTask<Uri, Void, String>{

        private Context context; // 컨택스트를 받을 변수

        private String serverUrl = "http://3.38.213.196/meeting/inputMeeting.php"; // 업로드될 서버 위치
        private String TAG = "ImageUploader";

        public galleryTask(Context context){ // 생성자를 통해 context를 받아옴
            this.context = context;
        }

        // 데이터 통신이 전송되었을때 함수
        @Override
        protected String doInBackground(Uri... uris) {
            if (uris.length == 0 || uris[0] == null){ // uri 가 있는지 확인
                return "uri 가 없습니다.";
            }

            OkHttpClient client = new OkHttpClient(); // okhttp 객체 생성

            try {
                // Uri에서 실제 파일 경로 가져오기
                String filePath = FileUtils.getPathFromUri(context, uris[0]);
                File file = new File(filePath); // 파일을 다루기 위해 파일 객체를 가져옴

                RequestBody requestBody = new MultipartBody.Builder() // 요청 body에 넣을 내용 추가
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", "image.png", RequestBody.create(MediaType.parse("image/*"), file))
                        .addFormDataPart("user_id", id)
                        .addFormDataPart("x", x)
                        .addFormDataPart("y", y)
                        .addFormDataPart("name", meetingName.getText().toString())
                        .addFormDataPart("content", meetingContent.getText().toString())
                        .addFormDataPart("member", sNum)
                        .addFormDataPart("address", roadAddressName)
                        .addFormDataPart("placeName", placeName)
                        .build();

                Request request = new Request.Builder()
                        .url(serverUrl)
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    return response.body().string(); // 서버 응답 반환
                } else {
                    return "이미지 업로드 실패";
                }
            } catch (IOException e){
                e.printStackTrace();
                return "오류 발생 : " + e.getMessage();
            }
        }

        // 데이터 통신이 수신 되었을때 함수
        @Override
        protected void onPostExecute(String result){
            Toast.makeText(inputMeeting.this, result, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(inputMeeting.this, main.class);
            intent.putExtra("where", "1"); // 어디로 강제 이동할 것인지 설정
            startActivity(intent);
        }
    }
    // 이미지 없이 저장
    private class Task extends AsyncTask<Void, Void, String>{

        // 수정 서버 uri
        String serverUrl = "http://3.38.213.196/meeting/inputMeeting.php";

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            RequestBody body = new FormBody.Builder()
                    .add("user_id", id)
                    .add("x", x)
                    .add("y", y)
                    .add("name", meetingName.getText().toString())
                    .add("content", meetingContent.getText().toString())
                    .add("member", sNum)
                    .add("address", roadAddressName)
                    .add("placeName", placeName)
                    .build();

            Request request = new Request.Builder()
                    .url(serverUrl)
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e){
                e.printStackTrace();
                return "오류";
            }
        }

        @Override
        protected void onPostExecute(String result){
            Toast.makeText(inputMeeting.this, result, Toast.LENGTH_SHORT).show();
            System.out.println(result);
            Intent intent = new Intent(inputMeeting.this, main.class);
            intent.putExtra("where", "1"); // 어디로 강제 이동할 것인지 설정
            startActivity(intent);
        }
    }
}