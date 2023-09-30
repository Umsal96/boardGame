package com.example.boardgame;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.boardgame.utility.FileUtils;
import com.example.boardgame.utility.NetworkManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class profileModify extends AppCompatActivity {

    private Button checkNick; // 닉네임 중복검사
    private Button toLogin; // 닉네임 수정 확인 버튼
    private Button chooseImg; // 이미지 선택
    private TextView nowNick; // 지금 닉네임
    private EditText inputNewNick; // 수정할 닉네임 입력
    private CircleImageView circle; // 프로필 이미지
    private TextView noticeNick; // 닉네임 관련 텍스트
    boolean bNick = false;
    String nick;
    String id;
    String Suri; // 프로필 이미지 url
    int skyBlueButton = Color.parseColor("#3498DB");
    // 색깔 지정 회색
    int grayButton = Color.parseColor("#CCCCCC");
    private Uri imageUri; // 이미지 URI를 저장하기 위한 변수 추가
    private static final int CAMERA_PERMISSION_REQUEST = 123; // 원하는 값으로 변경 가능
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 125;

    private final int MY_PERMISSIONS_REQUEST_GALLERY = 124;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_modify);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없으면 권한 요청 대화 상자 표시
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        }

        // Ui 연결
        checkNick = findViewById(R.id.checkNick);
        toLogin = findViewById(R.id.toLogin);
        nowNick = findViewById(R.id.nowNick);
        inputNewNick = findViewById(R.id.inputNewNick);
        circle = findViewById(R.id.circle);
        noticeNick = findViewById(R.id.noticeNick);
        chooseImg = findViewById(R.id.chooseImg);

        toLogin.setEnabled(true);
        toLogin.setBackgroundColor(skyBlueButton);

        // 1. 쉐어드 프리퍼런스를 사용하기위해 UserData 라는 이름의 파일을 가져옴
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        // 2. 쉐어드 프리퍼런스에서 nickname라는 키로 정보를 가져옴 만약 nickname 이라는 키가 없을때 ""을 가져옴
        nick = sharedPreferences.getString("nickname", "");
        // 3. 쉐어드 프리퍼런스에서 userId 키로 정보를 가져옴 만약 userId 이라는 키가 없을때 ""을 가져옴
        id = sharedPreferences.getString("userId", "");
        // 4. 쉐어드 프리퍼런스에서 url 키로 정보를 가져옴 만약 userId 이라는 키가 없을때 null을 가져옴
        String uri = sharedPreferences.getString("url", null);

        // uri 가 null 이 아닐경우 만 실행
        if (uri != null && !uri.equals("null") && !uri.equals("")) {
            System.out.println("실행합니다.");
            // 이미지 url 설정
            String url = "http://3.38.213.196" + uri;
            System.out.println("url : " + url);
            // Glide 라이브러리를 이용해서 이미지를 가져옴
            Glide.with(this).load(url).into(circle);
        } else {
            System.out.println("이미지가 없습니다.");
            circle.setImageResource(R.drawable.img2);
        }

        nowNick.setText(nick);

        checkNick.setBackgroundColor(grayButton);

        checkNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NickNetworkTask().execute();
            }
        });

        // ActivityResultLauncher 초기화
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Bitmap photo = (Bitmap) data.getExtras().get("data");
                            circle.setImageBitmap(photo);

                            // 이미지를 서버에 업로드하는 AsyncTask 시작
                            BitmapUpload bitmapUpload = new BitmapUpload();
                            bitmapUpload.uploadImage(photo);
//                            new ImageUploader().execute(photo);
                        }
                    }
                });
        // ActivityResultLauncher 초기화
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            // 선택한 이미지를 이미지뷰에 표시
                            circle.setImageURI(selectedImageUri);
                            // 이미지를 서버에 업로드하는 작업을 여기에 추가
                            UriUpload uriUpload = new UriUpload(getApplicationContext());
                            uriUpload.uploadImage(selectedImageUri);
//                                    new ImageUploaderUri(getApplicationContext()).execute(selectedImageUri);
                        }
                    }
                });


        inputNewNick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputNewNick.setBackgroundResource(R.drawable.border_layout);
                String nick = inputNewNick.getText().toString(); // 입력한 새로운 닉네임
                int nickLength = nick.length();
                toLogin.setEnabled(false);
                toLogin.setBackgroundColor(grayButton);
                if(nickLength == 0){
                    toLogin.setEnabled(true);
                    toLogin.setBackgroundColor(skyBlueButton);
                } else if(nickLength > 0 ){
                    toLogin.setEnabled(false);
                    checkNick.setEnabled(true);
                    checkNick.setBackgroundColor(skyBlueButton);
                    noticeNick.setText("중복검사를 해주세요");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // chooseImg 버튼 클릭시 실행되는 (이벤트 카메라 이미지)
        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseDialog();
            }
        });

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 확인 버튼을 눌렀을때 어디 탭으로 이동할지 정하는 인텐트 5면 인텐트
                if(bNick){ // 닉네임이 수정이 되었다면 닉네임을 수정한뒤 프로필 사이트로 이동
                    new ChangeNick().execute();
                } else { // 닉네임을 수정하지 않았다면 그냥 프로필 시이트로 이동
                    Intent intent = new Intent(profileModify.this, main.class);
                    intent.putExtra("where", 5);
                    startActivity(intent);
                    finish();
                }
            }
        });

    } // end onCreate

    // 갤러리 이미지 기본 이미지 선택 할 수 있는 다이얼로그 표시 함수
    void showChooseDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("알림");

        alertDialogBuilder.setMessage("갤러리 이미지 기본 이미지 선택해주세요");

        alertDialogBuilder.setPositiveButton("카메라", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkCameraPermission();
            }
        });

        alertDialogBuilder.setNegativeButton("갤러리", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkGalleryPermission();
            }
        });

        alertDialogBuilder.setNeutralButton("기본 이미지", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // SharedPreferences 에 정보 저장
                new noImgTask().execute();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            System.out.println("카메라 없음");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            System.out.println("카메라 실행");
            openCamera();
        }
    }

    private void checkGalleryPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
            System.out.println("안녕하세요");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    MY_PERMISSIONS_REQUEST_GALLERY);
            System.out.println("안녕하세요2");
        } else {
            System.out.println("안녕히 가세요");
            openGallery();
        }
    }

    // 닉네임 중복검사
    private class NickNetworkTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            String nick = inputNewNick.getText().toString();
            NetworkManager networkManager = new NetworkManager();
            String serverUrl = "http://3.38.213.196/userJoin/nickCheck.php?nick=" + nick;
            return networkManager.fetchDataFromServer(serverUrl);
        }

        @Override
        protected void onPostExecute(String result){
            if(result.equals("1")){
                noticeNick.setText("사용이 가능한 닉네임 입니다.");
                inputNewNick.setBackgroundResource(R.drawable.ok_border_layout);
                bNick = true;
                toLogin.setEnabled(true);
                toLogin.setBackgroundColor(skyBlueButton);
            } else {
                noticeNick.setText("중복된 닉네임입니다.");
            }
        }
    }

    // 닉네임 수정 통신
    private class ChangeNick extends AsyncTask<Void, Void, Integer>{

        // 닉네임 수정 서버 uri
        String serverUrl = "http://3.38.213.196/userJoin/nickCh.php";
        // 새로 입력한 닉네임을 Snick 이라는 변수에 담는다
        String Snick = inputNewNick.getText().toString();

        // Okhttp 를 이용해서 통신에 닉네임과 고유 아이디를 통신의 body에 담는다
        @Override
        protected Integer doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            RequestBody body = new FormBody.Builder()
                    .add("nick", Snick)
                    .add("id", id)
                    .build();

            Request request = new Request.Builder()
                    .url(serverUrl)
                    .post(body)
                    .build();

            try{
                Response response = client.newCall(request).execute();
                return response.code();
            } catch (IOException e){
                e.printStackTrace();
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer statusCode){
            if(statusCode != null && statusCode.equals(200)){
                System.out.println("수정되었습니다.");
                String Snick = inputNewNick.getText().toString();
                SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("nickname", Snick);
                editor.apply();
                Intent intent = new Intent(profileModify.this, main.class);
                intent.putExtra("where", 5);
                startActivity(intent);
                showToastMessage("수정이 완료되었습니다");
                finish();
            }else {
                System.out.println("수정 실패");
            }
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(cameraIntent);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }



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

    private class UriUpload{
        private Context context;
        private String serverUrl = "http://3.38.213.196/Utility/imageUpload.php";
        private String TAG = "ImageUploader";

        public UriUpload(Context context) {
            this.context = context;
        }

        public void uploadImage(Uri uri){
            if (uri == null) {
                // 이미지가 없을 경우 처리
                return;
            }
            OkHttpClient client = new OkHttpClient();

            // Uri에서 실제 파일 경로 가져오기
            String filePath = FileUtils.getPathFromUri(context, uri);
            File file = new File(filePath);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", "image.png", RequestBody.create(MediaType.parse("image/*"), file))
                    .addFormDataPart("user_id", id)
                    .build();

            Request request = new Request.Builder()
                    .url(serverUrl)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        onUploadSuccess(responseBody);  // 성공적으로 업로드된 경우 처리할 메서드 호출
                    } else {
                        onUploadFailure(null);  // 업로드 실패 시 처리할 메서드 호출
                    }
                }
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    onUploadFailure(e.getMessage());  // 오류 발생 시 처리할 메서드 호출
                }
            });
        }

        private void onUploadSuccess(String result) {
            runOnUiThread(new Runnable() {  // UI 업데이트를 위해 runOnUiThread() 사용
                public void run() {
                    Toast.makeText(profileModify.this, result, Toast.LENGTH_SHORT).show();
                    System.out.println(result);
                    String url = "http://3.38.213.196" + result;
                    System.out.println("url : " + url);
                    Glide.with(getApplicationContext()).load(url).into(circle);
                    SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("url", result);
                    editor.apply();
                }
            });
        }

        private void onUploadFailure(String errorMessage) {
            runOnUiThread(new Runnable() {  // UI 업데이트를 위해 runOnUiThread() 사용
                public void run() {
                    Toast.makeText(profileModify.this, errorMessage != null ? errorMessage : "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    // 비트맵 방식으로 사진을 가져왔을때 이미지 업로드 코드
    private class BitmapUpload{
        public void uploadImage(Bitmap bitmap){
            if(bitmap == null){
                return;
            }

            // 이미지를 ByteArray로 변환
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            // 서버의 URL 설정
            String serverUrl = "http://3.38.213.196/Utility/imageUpload.php";

            // OkHttpClient 객체 생성
            OkHttpClient client = new OkHttpClient();

            // RequestBody 생성
            MediaType mediaType = MediaType.parse("image/*");

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", "image.png", RequestBody.create(byteArray, mediaType))
                    .addFormDataPart("user_id", id)
                    .build();

            Request request = new Request.Builder()
                    .url(serverUrl)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.d("s", "결과");
                        onUploadSuccess(responseBody);  // 성공적으로 업로드된 경우 처리할 메서드 호출
                    } else {
                        onUploadFailure("이미지 업로드 실패: " + response.code());  // 업로드 실패 시 처리할 메서드 호출
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    onUploadFailure("오류 발생 : " + e.getMessage());  // 오류 발생 시 처리할 메서드 호출
                }
            });
        }

        private void onUploadSuccess(String result) {
            runOnUiThread(new Runnable() {  // UI 업데이트를 위해 runOnUiThread() 사용
                public void run() {
                    Toast.makeText(profileModify.this, result, Toast.LENGTH_SHORT).show();
                    System.out.println(result);
                    String url = "http://3.38.213.196" + result;
                    System.out.println("url : " + url);
                    Glide.with(getApplicationContext()).load(url).into(circle);
                    SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("url", result);
                    editor.apply();
                }
            });
        }

        private void onUploadFailure(String errorMessage) {
            runOnUiThread(new Runnable() {  // UI 업데이트를 위해 runOnUiThread() 사용
                public void run() {
                    Toast.makeText(profileModify.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


//    private class ImageUploader extends AsyncTask<Bitmap, Void, String>{
//
//        @Override
//        protected String doInBackground(Bitmap... bitmaps) {
//
//            if(bitmaps.length == 0 || bitmaps[0] == null){
//                return "이미지가 없습니다.";
//            }
//
//            try{
//                Bitmap bitmap = bitmaps[0];
//
//                // 이미지를 ByteArray로 변환
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                byte[] byteArray = byteArrayOutputStream.toByteArray();
//
//                // 서버의 url 설정
//                String serverUrl = "http://3.38.213.196/Utility/imageUpload.php";
//
//                // OkHttpClient 객체 생성
//                OkHttpClient client = new OkHttpClient();
//
//                // RequestBody 생성
//                MediaType mediaType = MediaType.parse("image/*");
//                RequestBody requestBody = new MultipartBody.Builder()
//                        .setType(MultipartBody.FORM)
//                        .addFormDataPart("image", "image.png", RequestBody.create(byteArray, mediaType))
//                        .addFormDataPart("user_id", id)
//                        .build();
//
//                Request request = new Request.Builder()
//                        .url(serverUrl)
//                        .post(requestBody)
//                        .build();
//
//                Response response = client.newCall(request).execute();
//                // 서버 응답 출력
//                if (response.isSuccessful()) {
//                    String responseBody = response.body().string();
//                    Log.d("s", "결과");
//                    return responseBody; // 서버 응답 반환
//                } else {
//                    return "이미지 업로드 실패: " + response.code();
//                }
//            }catch (IOException e){
//                e.printStackTrace();
//                return "오류 발생 : " + e.getMessage();
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String result){
//            Toast.makeText(profileModify.this, result, Toast.LENGTH_SHORT).show();
//            System.out.println(result);
//            String url = "http://3.38.213.196" + result;
//            System.out.println("url : " + url);
//            // Glide 라이브러리를 이용해서 이미지를 가져옴
//            Glide.with(getApplicationContext()).load(url).into(circle);
//            SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("url", result);
//            editor.apply();
//        }
//    }

    private class noImgTask extends AsyncTask<Void, Void, String>{

        String serverUrl = "http://3.38.213.196/Utility/imageRemove.php";

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            RequestBody body = new FormBody.Builder()
                    .add("user_id", id)
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
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result){
            SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("url", null);
            editor.apply();
            circle.setImageResource(R.drawable.img2);
        }
    }

    private void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }
}