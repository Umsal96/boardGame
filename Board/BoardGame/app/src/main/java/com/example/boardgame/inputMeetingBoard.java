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

public class inputMeetingBoard extends AppCompatActivity {

    private ImageButton backPage;
    private TextView meetingTitle;
    private Button inputButton;
    private TextView boardTitle;
    private TextView boardContent;
    private TextView boardType;
    private Button inputImage;
    private ViewPager2 imageViewPager;
    private int UserId; // 현재 로그인한 유저의 고유 Id
    private TextView currentPage;
    private TextView totalPage;
    private ArrayList<Uri> imageDataList = new ArrayList<>();
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    String[] types;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 125;
    private final int MY_PERMISSIONS_REQUEST_GALLERY = 124;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_meeting_board);

        backPage = findViewById(R.id.backPage);
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
        int meetingId = intent.getIntExtra("meetingId", 0);
        int leaderId = intent.getIntExtra("leaderId", 0);

        System.out.println("모임의 고유 아이디 : " + meetingId);
        System.out.println("inputMeetingBoard 안의 리더 고유 아이디 : " + leaderId);

        // 쉐어드 프리퍼런스에 있는 유저의 아이디를 가져옴
        // 1. 쉐어드 프리퍼런스를 사용하기위해 UserData 라는 이름의 파일을 가져옴
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        // 쉐어드 프리퍼런스에 있는 userId 라는 키값을 가지고 있는 값을 가져오고 가져온 값을 int형으로 변환함
        UserId = Integer.parseInt(sharedPreferences.getString("userId", ""));

        getMeetingName(meetingId);

        // 카테고리 선택 다이얼로그 실행
        showTypeDialog(leaderId);

        ImageViewPagerAdapter imageViewPagerAdapter = new ImageViewPagerAdapter(imageDataList);
        imageViewPager.setAdapter(imageViewPagerAdapter);
        imageViewPagerAdapter.setOnCancelClickListener(new ImageViewPagerAdapter.OnCancelClickListener() {
            @Override
            public void onCancelClick(int position) {
                System.out.println("몇번째 아이템 입니다. : " + position);
                imageDataList.remove(position);
                imageViewPagerAdapter.notifyItemRemoved(position);
                totalPage.setText(String.valueOf(imageDataList.size()));
            }
        });

        inputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = boardTitle.getText().toString();
                String content = boardContent.getText().toString();

                int titleLength = title.length();
                int contentLength = content.length();

                System.out.println("제목 길이 판별 : " + titleLength);
                System.out.println("내용 길이 판별" + contentLength);

                if(titleLength < 1){
                    BoardAlertDialog("제목을 입력해주세요", boardTitle);
                } else if (contentLength < 1) {
                    BoardAlertDialog("내용을 입력해주세요", boardContent);
                }else {
                    inputBoard(meetingId);
                }

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
                        totalPage.setText(String.valueOf(imageDataList.size()));
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
                            totalPage.setText(String.valueOf(imageDataList.size()));

                        }else {
                            // 하난의 이미지를 선택한 경우
                            Uri imageUri = result.getData().getData();
                            imageDataList.add(imageUri);
                        }
                    }
                });

        boardType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeDialog(leaderId);
            }
        });

        imageViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                currentPage.setText(String.valueOf(position + 1));
            }
        });


        // 이미지 업로드 버튼을 눌렀을떄 실행되는 메소드

        inputImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                checkGalleryPermission();
                showChooseImgDialog(); // 어떤 방식으로 이미지를 가져올것인지 다이얼로그 표시
            }
        });

        // 원래 있던곳으로 돌아가는 이벤트
        backPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(inputMeetingBoard.this, getMeeting.class);
                System.out.println("inputMeetingBoard : " + meetingId);
                intent1.putExtra("where", 2);
                intent1.putExtra("id", meetingId);
                startActivity(intent1);
            }
        });
    }

    private void inputBoard(int meetingId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/meetingBoard/inputMeetingBoard.php").newBuilder();
        String url = urlBuilder.build().toString();

        RequestBody requestBody;

        if(imageDataList.size() > 0){
            // 이미지가 있을경울
            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("userId", String.valueOf(UserId))
                    .addFormDataPart("meetingId", String.valueOf(meetingId))
                    .addFormDataPart("boardTitle", boardTitle.getText().toString())
                    .addFormDataPart("boardContent", boardContent.getText().toString())
                    .addFormDataPart("boardType", boardType.getText().toString());


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
                    .add("userId", String.valueOf(UserId))
                    .add("meetingId", String.valueOf(meetingId))
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

                    System.out.println("meetingId : " + meetingId);

                    Intent intent = new Intent(inputMeetingBoard.this, getMeeting.class);
                    intent.putExtra("where", 2);
                    intent.putExtra("id", meetingId);
                    startActivity(intent);
                }
            }
        });
    }

    // 갤러리 이미지 기본 이미지 선택 할 수 있는 다이얼 로그 표시 함수
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

    // 글의 카테고리 선택할수 있는 아이얼로그 표시하는 메소드
    public void showTypeDialog(int leaderId){
        if(leaderId != UserId){
            System.out.println("모임장이 아닙니다.");
            types = getResources().getStringArray(R.array.boardType);
        }else {
            System.out.println("모임장이 맞습니다.");
            types = getResources().getStringArray(R.array.boardLeaderType);
        }
        builder = new AlertDialog.Builder(inputMeetingBoard.this);
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

    // 제목이나 내용을 입력하지 않았을때 알람이 나오는 함수
    private void BoardAlertDialog(String message, TextView targetView){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(inputMeetingBoard.this);
        builder1.setMessage(message)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        targetView.requestFocus();
                    }
                });
        AlertDialog dialog = builder1.create();
        dialog.show();
    }

}