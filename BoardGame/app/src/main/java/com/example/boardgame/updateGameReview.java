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
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.boardgame.Adapter.ReviewViewPagerAdapter;
import com.example.boardgame.Adapter.ViewPagerAdapter;
import com.example.boardgame.item.GameReviewItem;
import com.example.boardgame.item.ImageItem;
import com.example.boardgame.utility.FileUtils;
import com.example.boardgame.utility.JsonToGetData;

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

public class updateGameReview extends AppCompatActivity {

    private ImageButton backPage;
    private Button inputReview;
    private RatingBar inputRatingBar;
    private EditText reviewContent;
    private TextView nowNum;
    private ViewPager2 reviewInputViewPager;
    private ImageButton inputImageButton;
    private ArrayList<Uri> uriImageDataList = new ArrayList<>();
    private ArrayList<ImageItem> stringImageDataList = new ArrayList<>();
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 125;
    private final int MY_PERMISSIONS_REQUEST_GALLERY = 124;
    GameReviewItem item;
    String[] parts;
    ReviewViewPagerAdapter reviewViewPagerAdapter;
    private String currentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_game_review);

        backPage = findViewById(R.id.backPage);
        inputReview = findViewById(R.id.inputReview);
        inputRatingBar = findViewById(R.id.inputRatingBar);
        reviewContent = findViewById(R.id.reviewContent);
        nowNum = findViewById(R.id.nowNum);
        reviewInputViewPager = findViewById(R.id.reviewInputViewPager);
        inputImageButton = findViewById(R.id.inputImageButton);

        Intent intent = getIntent();
        int reviewId = intent.getIntExtra("reviewId", 0);

        inputImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseImgDialog(); // 어떤 방식으로 이미지를 가져올것인지 다이얼로그 표시
            }
        });

        reviewViewPagerAdapter = new ReviewViewPagerAdapter(stringImageDataList, uriImageDataList);
        reviewViewPagerAdapter.setOnCancelClickListener(new ViewPagerAdapter.OnCancelClickListener() {
            @Override
            public void onCancelClick(int position) {
                reviewViewPagerAdapter.notifyDataSetChanged();
            }
        });

        reviewInputViewPager.setAdapter(reviewViewPagerAdapter);

        inputReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyReview(reviewId);
            }
        });

        inputRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                nowNum.setText(String.valueOf((int) rating));
            }
        });

        // camera 실행
        // ActivityResultLauncher 초기화
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Uri photoUri = Uri.fromFile(new File(currentPhotoPath));
                        System.out.println("Uri : " + photoUri);
                        uriImageDataList.add(photoUri);
                        reviewInputViewPager.setVisibility(View.VISIBLE);
                        reviewViewPagerAdapter.notifyDataSetChanged();
                    }
                });

        // 갤러리 실행
        // ActivityResultLauncher 초기화
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK && result.getData() != null){
                        ClipData clipData = result.getData().getClipData();
                        if(clipData != null){
                            // 여러 이미지를 선택한 경우
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri imageUri = clipData.getItemAt(i).getUri();
                                uriImageDataList.add(imageUri);
                                System.out.println(imageUri.toString());
                                System.out.println("여러이미지");
                            }
                            reviewInputViewPager.setVisibility(View.VISIBLE);
                            reviewViewPagerAdapter.notifyDataSetChanged();
                            System.out.println("최대 길이 : " + uriImageDataList.size());

                        }else {
                            // 하난의 이미지를 선택한 경우
                            Uri imageUri = result.getData().getData();
                            uriImageDataList.add(imageUri);
                        }
                    }
                });

        getGameReview(reviewId);
    }

    private void modifyReview(int reviewId){
        System.out.println("getMeetingBoardModify image length : " + stringImageDataList.size());

        int lang = stringImageDataList.size() + uriImageDataList.size();

        System.out.println("이미지 길이 : " + lang);

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/gameReview/getGameUpdate.php").newBuilder();
        String url = urlBuilder.build().toString();

        RequestBody requestBody;

        if(uriImageDataList.size() > 0){
            System.out.println("이미지가 있어용");
            // 이미지가 있을경울
            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("reviewId", String.valueOf(reviewId))
                    .addFormDataPart("reviewContent", reviewContent.getText().toString())
                    .addFormDataPart("reviewGrade", nowNum.getText().toString())
                    .addFormDataPart("imageOrder", String.valueOf(lang));

            // 각 이미지를 별도의 파트로 추가
            for (int i = 0; i < uriImageDataList.size(); i++) {
                Uri imageUri = uriImageDataList.get(i);

                String imageUriString = imageUri.toString();

//                String filePath = FileUtils.getPathFromUri(getApplicationContext(), imageUri);

                String filePath;

                // imageUri가 'file://'로 시작하는 경우
                if (imageUriString.startsWith("file://")) {
                    // 'file://'을 제거하여 실제 파일 경로를 얻음
                    filePath = imageUriString.substring(7);
                } else {
                    filePath = FileUtils.getPathFromUri(getApplicationContext(), imageUri);
                }

                File file = new File(filePath);
                multipartBuilder.addFormDataPart("image" + (i + lang), "image"+(i + lang)+".png", RequestBody.create(MediaType.parse("image/*"), file));
            }


            requestBody = multipartBuilder.build();

        }else {
            // 이미지가 없을경우
            System.out.println("이미지가 없어용");

            // POST 요청 본문을 생성
            requestBody = new FormBody.Builder()
                    .add("reviewId", String.valueOf(reviewId))
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

                    Intent intent = new Intent(updateGameReview.this, getGame.class);
                    intent.putExtra("gameId", item.getGame_seq());
                    startActivity(intent);
                }
            }
        });
    }

    private void showChooseImgDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this); // 다이얼로그 객체 생성
        alertDialogBuilder.setTitle("알림"); // 다이얼로그의 제목 설정

        alertDialogBuilder.setMessage("갤러리 카메라 중 선택해주세요"); // 다이얼로그에 나올 메시지 설정

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

    // 카메라 실행
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

    private void getGameReview(int reviewId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/gameReview/getGameReview.php").newBuilder();
        urlBuilder.addQueryParameter("reviewId", String.valueOf(reviewId));
        String url = urlBuilder.build().toString();
        JsonToGetData jtg = new JsonToGetData();

        Request request = new Request.Builder()
                .url(url)
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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            item = jtg.jsonGetToGetGameReview(responseData);

                            inputRatingBar.setRating(item.getReview_grade());
                            nowNum.setText(String.valueOf((float) item.getReview_grade()));
                            reviewContent.setText(item.getReview_content());

                            String img = item.getImage_urls();
                            String seqss = item.getTo_seqs();

                            // 이미지가 있는지 확인
                            if (img != null && !img.equals("null") && !img.equals("")) {
                                // 이미지가 있다면
                                parts = img.split(",");
                                String[] sub = seqss.split(",");

                                int lang = parts.length;
                                for (int i = 0; i < parts.length; i++) {
                                    System.out.println("이미지 uri : " + parts[i]);
                                }

                                stringImageDataList.clear();

                                for (int i = 0; i < parts.length; i++) {
                                    ImageItem imageItem = new ImageItem();
                                    imageItem.setImage_url(parts[i]);

                                    imageItem.setImage_seq(Integer.parseInt(sub[i]));

                                    stringImageDataList.add(imageItem);
                                }

                                if(reviewViewPagerAdapter == null){
                                    // 이미지 어뎁터가 초기화 되지 않은경우
                                    reviewViewPagerAdapter.setData(stringImageDataList, uriImageDataList);

                                    reviewInputViewPager.setAdapter(reviewViewPagerAdapter);
                                } else{
                                    // 이미지 어댑터가 이미 초기화된 경우
                                    reviewViewPagerAdapter.setData(stringImageDataList, uriImageDataList);
                                    reviewViewPagerAdapter.notifyDataSetChanged();

                                }

                                reviewInputViewPager.setAdapter(reviewViewPagerAdapter);
                                System.out.println("이미지의 갯수 : " + lang);
                                reviewViewPagerAdapter.notifyDataSetChanged();
                                reviewInputViewPager.setVisibility(View.VISIBLE);
                            } else {
                                System.out.println("이미지가 없습니다.");
                                reviewInputViewPager.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

    }
}