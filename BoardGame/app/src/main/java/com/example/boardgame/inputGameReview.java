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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.boardgame.Adapter.ImageViewPagerAdapter;
import com.example.boardgame.utility.FileUtils;

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

public class inputGameReview extends AppCompatActivity {

    private ImageButton backPage;
    private ImageButton inputImageButton;
    private Button inputReview;
    private RatingBar inputRatingBar;
    private EditText reviewContent;
    private ViewPager2 reviewInputViewPager;
    private TextView nowNum;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 125;
    private final int MY_PERMISSIONS_REQUEST_GALLERY = 124;
    private ArrayList<Uri> imageDataList = new ArrayList<>();
    private String currentPhotoPath;
    private ImageViewPagerAdapter imageViewPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_game_review);

        backPage = findViewById(R.id.backPage); // 뒤로 가기 버튼
        inputImageButton = findViewById(R.id.inputImageButton); // 이미지 입력 버튼
        inputReview = findViewById(R.id.inputReview); // 리뷰 입력 버튼
        inputRatingBar = findViewById(R.id.inputRatingBar); // 평점 입력
        reviewContent = findViewById(R.id.reviewContent); // 리뷰 내용
        reviewInputViewPager = findViewById(R.id.reviewInputViewPager);
        nowNum = findViewById(R.id.nowNum);

        Intent intent = getIntent();
        int gameId = intent.getIntExtra("gameId", 0);

        // 쉐어드 프리퍼런스에 있는 유저의 아이디를 가져옴
        // 1. 쉐어드 프리퍼런스를 사용하기위해 UserData 라는 이름의 파일을 가져옴
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        // 쉐어드 프리퍼런스에 있는 userId 라는 키값을 가지고 있는 값을 가져오고 가져온 값을 int형으로 변환함
        int userId = Integer.parseInt(sharedPreferences.getString("userId", ""));

        imageViewPagerAdapter = new ImageViewPagerAdapter(imageDataList);
        reviewInputViewPager.setAdapter(imageViewPagerAdapter);
        inputRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                nowNum.setText(String.valueOf((int) rating));
            }
        });

        imageViewPagerAdapter.setOnCancelClickListener(new ImageViewPagerAdapter.OnCancelClickListener() {
            @Override
            public void onCancelClick(int position) {
                imageDataList.remove(position);
                imageViewPagerAdapter.notifyItemRemoved(position);
            }
        });

        // 리뷰 작성 버튼 눌렀을때 실행되는 이벤트
        inputReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 input(gameId, userId);
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
                        imageDataList.add(photoUri);
                        imageViewPagerAdapter.notifyDataSetChanged();
//                        totalPage.setText(String.valueOf(imageDataList.size()));
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
//                            totalPage.setText(String.valueOf(imageDataList.size()));

                        }else {
                            // 하난의 이미지를 선택한 경우
                            Uri imageUri = result.getData().getData();
                            imageDataList.add(imageUri);
                        }
                    }
                });
    }

    private void input(int gameId, int userId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/gameReview/inputGameReview.php").newBuilder();
        String url = urlBuilder.build().toString();

        RequestBody requestBody;

        if(imageDataList.size() > 0){
            // 이미지가 있을 경우
            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("userId", String.valueOf(userId))
                    .addFormDataPart("gameId", String.valueOf(gameId))
                    .addFormDataPart("reviewContent", reviewContent.getText().toString())
                    .addFormDataPart("reviewGrade", nowNum.getText().toString());

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
        } else {
            // 이미지가 없을 경우
            // POST 요청 본문을 생성

            requestBody = new FormBody.Builder()
                    .add("userId", String.valueOf(userId))
                    .add("gameId", String.valueOf(gameId))
                    .add("reviewContent", reviewContent.getText().toString())
                    .add("reviewGrade", nowNum.getText().toString())
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

                    Intent intent = new Intent(inputGameReview.this, getGame.class);
                    intent.putExtra("gameId", gameId);
                    startActivity(intent);
                }
            }
        });
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
}