package com.example.boardgame;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.boardgame.utility.CustomTextWatcher;
import com.example.boardgame.utility.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
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

public class inputFood extends AppCompatActivity {
    private ImageView foodImg;
    private EditText inputFoodName;
    private EditText inputFoodPrice;
    private ImageButton inputImageButton;
    private Button inputFoodButton;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 125;
    private final int MY_PERMISSIONS_REQUEST_GALLERY = 124;
    private Uri imageData;
    private String currentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_food);

        foodImg = findViewById(R.id.foodImg); // 이미지 표시
        inputFoodName = findViewById(R.id.inputFoodName); // 음식 이름 입력
        inputFoodPrice = findViewById(R.id.inputFoodPrice); // 음식 가격 입력
        inputFoodPrice.addTextChangedListener(new CustomTextWatcher(inputFoodPrice));
        inputImageButton = findViewById(R.id.inputImageButton); // 이미지 입력 버틑
        inputFoodButton = findViewById(R.id.inputFoodButton); // 음식 정보 입력

        Intent intent = getIntent();
        int cafeId = intent.getIntExtra("cafeId", 0);

        inputFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputFood(cafeId);
            }
        });
        inputImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseImgDialog();
            }
        });

        // ActivityResultLauncher 초기화
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Uri photoUri = Uri.fromFile(new File(currentPhotoPath));
                        System.out.println("Uri : " + photoUri);
                        imageData = photoUri;
                        foodImg.setImageURI(imageData);
                    }
                });
        // ActivityResultLauncher 초기화
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK && result.getData() != null){
                        imageData = result.getData().getData();
                        foodImg.setImageURI(imageData);
                    }
                });
    }
    private void inputFood(int cafeId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/food/inputFood.php").newBuilder();
        String url = urlBuilder.build().toString();

        RequestBody requestBody;

        String st = inputFoodPrice.getText().toString().replace(",", "");
        System.out.println("가격 확인 : " + st);

        if(imageData != null){
            // 이미지가 있는 경우
            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("cafeId", String.valueOf(cafeId))
                    .addFormDataPart("foodName" , inputFoodName.getText().toString())
                    .addFormDataPart("foodPrice", st);

            String imageUriString = imageData.toString();

            String filePath;

            // imageUri가 'file://'로 시작하는 경우
            if (imageUriString.startsWith("file://")) {
                // 'file://'을 제거하여 실제 파일 경로를 얻음
                filePath = imageUriString.substring(7);
            } else {
                filePath = FileUtils.getPathFromUri(getApplicationContext(), imageData);
            }
            File file = new File(filePath);
            multipartBuilder.addFormDataPart("image", "image.png", RequestBody.create(MediaType.parse("image/*"), file));
            requestBody = multipartBuilder.build();
        }else{
            // 이미지가 없는 경우
            requestBody = new FormBody.Builder()
                    .add("cafeId", String.valueOf(cafeId))
                    .add("foodName", inputFoodName.getText().toString())
                    .add("foodPrice", st)
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
                    Intent intent = new Intent(inputFood.this, getCafe.class);
                    intent.putExtra("cafeId", cafeId);
                    intent.putExtra("where", 1);
                    startActivity(intent);
                }
            }
        });
    }
    // 카메라 갤러리 이미지 선택 다이얼로그 표시
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
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
}