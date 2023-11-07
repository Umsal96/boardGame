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
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.boardgame.Adapter.BoardImageViewPagerAdapter;
import com.example.boardgame.Adapter.ImageViewPagerAdapter;
import com.example.boardgame.Adapter.ViewPagerAdapter;
import com.example.boardgame.item.MeetingBoardDetailItem;
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

public class getMeetingBoardModify extends AppCompatActivity {

    private ImageButton backPage;
    private TextView meetingTitle;
    private Button inputButton; // 수정 버튼
    private TextView boardTitle; // 글 제목
    private TextView boardContent; // 글 내용
    private TextView boardType; // 글 종류
    private Button inputImage; // 이미지 입력
    private ViewPager2 imageViewPager;
    private TextView currentPage;
    private TextView totalPage;
    MeetingBoardDetailItem item;
    private ArrayList<Uri> uriImageDataList = new ArrayList<>();
    private ArrayList<String> stringImageDataList = new ArrayList<>();
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 125;
    private final int MY_PERMISSIONS_REQUEST_GALLERY = 124;
    AlertDialog.Builder builder;
    ViewPagerAdapter viewPagerAdapter;
    private String currentPhotoPath;
    String[] types;
    String[] parts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_meeting_board_modify);

        backPage = findViewById(R.id.backPage); // 뒤로 가기 버튼
        meetingTitle = findViewById(R.id.meetingTitle);
        inputButton = findViewById(R.id.inputButton);
        boardTitle = findViewById(R.id.boardTitle);
        boardContent = findViewById(R.id.boardContent);
        boardType = findViewById(R.id.boardType);
        inputImage = findViewById(R.id.inputImage); // 이미지 업로드 버튼
        imageViewPager = findViewById(R.id.imageViewPager);
        currentPage = findViewById(R.id.currentPage);
        totalPage = findViewById(R.id.totalPage);

        Intent intent = getIntent();
        int boardId = intent.getIntExtra("boardId", 0);
        int meetingId = intent.getIntExtra("meetingId", 0);
        int leaderId = intent.getIntExtra("leaderId", 0);

        System.out.println("getMeetingBoardModify leaderId : " + leaderId);

        System.out.println("getMeetingBoardModify boardId : " + boardId);

        // 쉐어드 프리퍼런스에 있는 유저의 아이디를 가져옴
        // 1. 쉐어드 프리퍼런스를 사용하기위해 UserData 라는 이름의 파일을 가져옴
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        // 쉐어드 프리퍼런스에 있는 userId 라는 키값을 가지고 있는 값을 가져오고 가져온 값을 int형으로 변환함
        int UserId = Integer.parseInt(sharedPreferences.getString("userId", ""));

        // 뒤로 가기 버튼
        backPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("getMeetingBoardModify boardId : " + boardId);
                Intent intent1 = new Intent(getMeetingBoardModify.this, getMeetingBoard.class);
                intent1.putExtra("boardId", boardId);
                startActivity(intent1);
            }
        });

        // 카테고리 클릭
        boardType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeDialog(leaderId, UserId);
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
                        viewPagerAdapter.notifyDataSetChanged();
                        totalPage.setText(String.valueOf(uriImageDataList.size() + stringImageDataList.size()));
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
                            viewPagerAdapter.notifyDataSetChanged();
                            System.out.println("최대 길이 : " + uriImageDataList.size());
                            totalPage.setText(String.valueOf(uriImageDataList.size() + stringImageDataList.size()));

                        }else {
                            // 하난의 이미지를 선택한 경우
                            Uri imageUri = result.getData().getData();
                            uriImageDataList.add(imageUri);
                        }
                    }
                });

        getMeetingBoard(boardId);

        getMeetingName(meetingId);

        inputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyBoard(boardId, leaderId);
            }
        });

        // 이미지 입력
        inputImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseImgDialog(); // 어떤 방식으로 이미지를 가져올것인지 다이얼로그 표시
            }
        });

        imageViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                currentPage.setText(String.valueOf(position + 1));
            }
        });
    } // end onCreate

    private void modifyBoard(int boardId, int leaderId){
        System.out.println("getMeetingBoardModify image length : " + stringImageDataList.size());

        int lang = stringImageDataList.size();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/meetingBoard/updateMeetingBoard.php").newBuilder();
        String url = urlBuilder.build().toString();

        RequestBody requestBody;

        if(uriImageDataList.size() > 0){
            System.out.println("이미지가 있어용");
            // 이미지가 있을경울
            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("boardId", String.valueOf(boardId))
                    .addFormDataPart("boardTitle", boardTitle.getText().toString())
                    .addFormDataPart("boardContent", boardContent.getText().toString())
                    .addFormDataPart("boardType", boardType.getText().toString())
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
                    .add("boardId", String.valueOf(boardId))
                    .add("boardTitle", boardTitle.getText().toString())
                    .add("boardContent", boardContent.getText().toString())
                    .add("boardType", boardType.getText().toString())
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

                    Intent intent = new Intent(getMeetingBoardModify.this, getMeetingBoard.class);
                    intent.putExtra("leaderId", leaderId);
                    intent.putExtra("boardId", boardId); // 게시글 고유 아이디
                    startActivity(intent);
                }
            }
        });
    }

    // 글의 카테고리 선택할수 있는 아이얼로그 표시하는 메소드
    private void showTypeDialog(int leaderId, int UserId){ // 모임장 고유 아이디, 로그인유저의 고유 아이디
        if(leaderId != UserId){
            System.out.println("모임장이 아닙니다.");
            types = getResources().getStringArray(R.array.boardType);
        }else {
            System.out.println("모임장이 맞습니다.");
            types = getResources().getStringArray(R.array.boardLeaderType);
        }
        builder = new AlertDialog.Builder(getMeetingBoardModify.this);
        builder.setTitle("게시글 카테고리");
        // 다이얼로그에 리스트 담기
        builder.setItems(types, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("선택한 글 카테고리는 : " + types[which]);
                boardType.setText(types[which]);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

    // 모임의 이름을 가져오는 메소드
    private void getMeetingName(int id){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/meeting/getMeetingTitle.php").newBuilder();
        urlBuilder.addQueryParameter("id", String.valueOf(id)); // url 쿼리에 id 라는 메개변수 추가
        String url = urlBuilder.build().toString();

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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            meetingTitle.setText(responseData);
                        }
                    });
                }
            }
        });
    } // end getMeetingName

    private void getMeetingBoard(int boardId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/meetingBoard/getMeetingBoard.php").newBuilder();
        urlBuilder.addQueryParameter("boardId", String.valueOf(boardId));
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
                            item = jtg.jsonGetToGetBoard(responseData);

                            // 글의 종류 설정
                            boardType.setText(item.getBoard_type());
                            // 글의 제목
                            boardTitle.setText(item.getBoard_title());
                            // 글의 내용
                            boardContent.setText(item.getBoard_content());

                            String img = item.getImage_urls();

                            // 이미지가 있는지 확인
                            if (img != null && !img.equals("null") && !img.equals("")) {
                                // 이미지가 있다면
                                parts = img.split(",");
                                int lang = parts.length;
                                for (int i = 0; i < parts.length; i++) {
                                    System.out.println("이미지 uri : " + parts[i]);
                                }

                                stringImageDataList.clear();

                                for (int i = 0; i < parts.length; i++) {
                                    stringImageDataList.add(parts[i]);
                                }

                                viewPagerAdapter = new ViewPagerAdapter(stringImageDataList, uriImageDataList, boardId);

                                viewPagerAdapter.setOnCancelClickListener(new ViewPagerAdapter.OnCancelClickListener() {
                                    @Override
                                    public void onCancelClick(int position) {
                                        totalPage.setText(String.valueOf(uriImageDataList.size() + stringImageDataList.size() - 1));
                                    }
                                });

                                imageViewPager.setAdapter(viewPagerAdapter);
                                System.out.println("이미지의 갯수 : " + lang);
                                totalPage.setText(String.valueOf(lang));
                                viewPagerAdapter.notifyDataSetChanged();
                                imageViewPager.setVisibility(View.VISIBLE);
                            } else {
                                System.out.println("이미지가 없습니다.");
                                imageViewPager.setVisibility(View.GONE);
                            }

                        }
                    });
                }
            }
        });
    }
}