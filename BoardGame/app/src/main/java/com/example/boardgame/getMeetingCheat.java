package com.example.boardgame;

import static android.app.Activity.RESULT_OK;

import static com.example.boardgame.service.ChattingSocketService.enterUser;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.boardgame.Adapter.ChattingAdapter;
import com.example.boardgame.item.ChatImg;
import com.example.boardgame.item.ChattingItem;
import com.example.boardgame.item.ReadItem;
import com.example.boardgame.item.UserItem;
import com.example.boardgame.service.ChattingSocketService;
import com.example.boardgame.utility.FileUtils;
import com.example.boardgame.utility.JsonToData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

public class getMeetingCheat extends Fragment {

    private EditText chatText;
    private Button sendChat;
    private ConstraintLayout imgLayout;
    private ImageButton moreImg;
    private ImageButton cameraImg;
    private ImageButton galleryImg;
    private String currentPhotoPath;
    private RecyclerView chatRecyclerView;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 125;
    private final int MY_PERMISSIONS_REQUEST_GALLERY = 124;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    ArrayList<UserItem> ui = new ArrayList<>();
    String receivedData;
    ArrayList<ChattingItem> ci = new ArrayList<>(); // 채팅방의
    int meetingId = 0; // 외부에서 받아온 모임 고유 아이디
    UserItem my = new UserItem();
    ChattingAdapter chattingAdapter;
    ArrayList<ReadItem> ri = new ArrayList<>(); // 채팅방의 읽음 기록
    int userId;
    private final BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            receivedData = intent.getStringExtra("receivedData"); // 받아온 소켓 채팅 서비스에서 넘어온 json 데이터
            JsonObject jsonObject = JsonParser.parseString(receivedData).getAsJsonObject();
            String action = jsonObject.get("action").getAsString();
            System.out.println("action : " + action);
            if("chat".equals(action)){
                int userSeq = jsonObject.get("userSeq").getAsInt();
                String content = jsonObject.get("content").getAsString();
                String date = jsonObject.get("chatTime").getAsString();
                int meetingSeq = jsonObject.get("meetingSeq").getAsInt();
                int messageSeq = jsonObject.get("seq").getAsInt();
                updateRead(meetingId, userId, messageSeq);
                if (meetingId == meetingSeq) { // 내가 보고있는 채팅방의 고유 아이디와 전성되어온 json 데이터중 채팅방 고유 아이디가 같을떄
                    ChattingItem chattingItem = new ChattingItem();
                    chattingItem.setMessage_seq(messageSeq);
                    chattingItem.setUser_seq(userSeq);
                    chattingItem.setMeeting_seq(meetingSeq);
                    chattingItem.setMessage_content(content);

                    chattingItem.setMessage_date(date);
                    // 유저의 프로필 사진과 닉네임을 가져옴
                    for (int i = 0; i < ui.size(); i++) {
                        if (ui.get(i).getUserSeq() == userSeq) {
                            chattingItem.setUser_url(ui.get(i).getUserUrl());
                            chattingItem.setUser_nickname(ui.get(i).getUserNick());
                            break;
                        }
                    }
                    ci.add(chattingItem);
                    chattingAdapter.notifyDataSetChanged();
                    chatRecyclerView.scrollToPosition(chattingAdapter.getItemCount() - 1); // 리사이클려뷰 마지막 아이템이 보이게

                }
            } else if ("enter".equals(action)) { // 들어왔다는 액션이 들어오면
                System.out.println("유저가 입장했습니다.");
//                try {
//                    Thread.sleep(300);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                System.out.println("meetingId : " + meetingId);
                updateRead(meetingId, userId, ci.get(ci.size() - 1).getMessage_seq());

                for (int i = 0; i < ci.size(); i++) {
                    if (ci.get(i).getMessage_read() >= 2){
                        System.out.println("채팅 아이템 정보 : " + ci.get(i).getMessage_content());
                        System.out.println("채팅 아이템 정보 : " + ci.get(i).getMessage_read());
                        System.out.println("=================================================");
                    }
                    // 현재 read -> 2  ui + 1 - enter.size()
                    //                          1
//                    if (ci.get(i).getMessage_read() >= (ui.size()) - (enterUser.size())){
//                        ci.get(i).setMessage_read(ci.get(i).getMessage_read() - enterUser.size());
//                    }
                }

                chattingAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(chattingAdapter.getItemCount() - 1); // 리사이클려뷰 마지막 아이템이 보이게
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_get_meeting_cheat, container, false);

        System.out.println("onCreateView");

        chatText = view.findViewById(R.id.chatText);
        sendChat = view.findViewById(R.id.sendChat);
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        moreImg = view.findViewById(R.id.moreImg);
        imgLayout = view.findViewById(R.id.imgLayout);
        cameraImg = view.findViewById(R.id.cameraImg);
        galleryImg = view.findViewById(R.id.galleryImg);

        // 데이터를 수신하기 위해 BroadcastReceiver를 등록
        IntentFilter intentFilter = new IntentFilter("com.example.boardgame.ACTION_DATA_RECEIVED");
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(dataReceiver, intentFilter);

        // 쉐어드 프리퍼런스에 있는 유저의 아이디를 가져옴
        // 1. 쉐어드 프리퍼런스를 사용하기위해 UserData 라는 이름의 파일을 가져옴
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        // 쉐어드 프리퍼런스에 있는 userId 라는 키값을 가지고 있는 값을 가져오고 가져온 값을 int형으로 변환함
        userId = Integer.parseInt(sharedPreferences.getString("userId", ""));

        Bundle bundle = getArguments();

        if (bundle != null) {
            meetingId = bundle.getInt("id", 0);
        }
//        getReadUser(meetingId, userId);
        getChattingList(meetingId);
        // 내가 채팅방에 입장했다는 정보를 소켓에 전송을 위한 json 작성

        String jsonEnter = enterChatRoom(userId);

        // 현재 채팅방 고유 아이디(모임 고유 아이디)를 서비스로 전송
        // 채팅방 고유 아이디가 json 의 meetingId 가 같으면 노티피케이션이 울리지 않게 하기위함
        Intent chatMeetingId = new Intent(getContext(), ChattingSocketService.class);
        chatMeetingId.putExtra("jsonEnter", jsonEnter);
        getContext().startService(chatMeetingId);

        // 같은 채팅방의 닉네임과 프로필을 가져옴
        getUsersInfo(meetingId, userId);

        System.out.println("getMeetingCheat meetingId: " + meetingId);
        sendChat.setEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        chatRecyclerView.setLayoutManager(linearLayoutManager);
        chattingAdapter = new ChattingAdapter(ci, userId);
        chatRecyclerView.setAdapter(chattingAdapter);

        // ActivityResultLauncher 초기화
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    ArrayList<Uri> cameraDataList = new ArrayList<>();
                    if (result.getResultCode() == RESULT_OK) {
                        Uri photoUri = Uri.fromFile(new File(currentPhotoPath));
                        System.out.println("Uri : " + photoUri);
                        cameraDataList.add(photoUri);
                        ChattingItem chattingItem = new ChattingItem();
                        chattingItem.setMeeting_seq(meetingId);
                        chattingItem.setMessage_date(getCurrentDateTime());
                        chattingItem.setUser_seq(userId);
                        chattingImg(chattingItem, cameraDataList);
                    }
                });
        // ActivityResultLauncher 초기화
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    ArrayList<Uri> galleryDataList = new ArrayList<>();
                    if(result.getResultCode() == RESULT_OK && result.getData() != null){
                        ClipData clipData = result.getData().getClipData();
                        if(clipData != null){
                            // 여러 이미지를 선택한 경우
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri imageUri = clipData.getItemAt(i).getUri();
                                galleryDataList.add(imageUri);
                            }
                        } else {
                            // 하난의 이미지를 선택한 경우
                            Uri imageUri = result.getData().getData();
                            galleryDataList.add(imageUri);
                        }
                        ChattingItem chattingItem = new ChattingItem();
                        chattingItem.setMeeting_seq(meetingId);
                        chattingItem.setMessage_date(getCurrentDateTime());
                        chattingItem.setUser_seq(userId);
                        chattingImg(chattingItem, galleryDataList);
                    }
                });
        chatText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            // 에딧텍스트가 수정되는 이벤트
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = chatText.getText().toString();
                int contentLength = content.length();
                if (contentLength == 0) {
                    sendChat.setEnabled(false);
                }
                if (contentLength >= 1) {
                    sendChat.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        int jMeetingSeq = meetingId;
        sendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChattingItem chattingItem = new ChattingItem();
                String content = chatText.getText().toString();
                String chatTime = getCurrentDateTime();
                // 어뎁터에 넣을 데이터를 작성
                chattingItem.setMeeting_seq(jMeetingSeq);
                chattingItem.setUser_seq(userId);
                chattingItem.setMessage_content(content);
                chattingItem.setMessage_date(chatTime);
                chattingItem.setUser_nickname(my.getUserNick());
                chattingItem.setUser_url(my.getUserUrl());

                chatText.setText("");

                inputChatting(chattingItem);
            }
        });
        moreImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgLayout.getVisibility() == View.VISIBLE){
                    imgLayout.setVisibility(View.GONE);
                }else{
                    imgLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        // 채팅 부분의 카메라 버튼 클릭
        cameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });
        // 채팅 부분의 갤러리 버튼 클릭
        galleryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGalleryPermission();
            }
        });

        return view;
    }
    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(dataReceiver);
        super.onPause();
        System.out.println("getMeetingCheat onPause");
        // 현재 채팅방 고유 아이디(모임 고유 아이디)를 서비스로 전송
        // 채팅방 고유 아이디가 json 의 meetingId 가 같으면 노티피케이션이 울리지 않게 하기위함
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("userSeq", userId);
        map.put("action", "out");
        map.put("meetingSeq", meetingId);
        map.put("meetingId", 0);
        String jsonEnter = gson.toJson(map);
        Intent chatMeetingId = new Intent(getContext(), ChattingSocketService.class);
        chatMeetingId.putExtra("jsonEnter", jsonEnter);
        getContext().startService(chatMeetingId);
        endUpdateRead(meetingId, userId);
    }

    // 카메라 사용 권한 있는지 확인용 함수
    private void checkCameraPermission(){
        if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            openCamera();
        }
    }

    // 갤러리 이미지 사용 권한 확인용 함수
    private void checkGalleryPermission(){
        if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),
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
            Uri photoURI = FileProvider.getUriForFile(getContext(),
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
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // 현재 로그인한 유저의 해당 채팅방의
    // 읽음 정보가 있는지 확인하고 없다면 정보를 입력하고
    // 있다면 읽음 정보를 업데이트
    private void getReadUser(int meetingId, int userId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/chat/getRead.php").newBuilder();
        urlBuilder.addQueryParameter("meeting_seq", String.valueOf(meetingId)); // url 쿼리에 id 라는 메개변수 추가
        urlBuilder.addQueryParameter("user_seq", String.valueOf(userId));
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
                    try {
                        JSONObject json = new JSONObject(responseData);

                        int recordCount = json.getInt("record_count");

                        if(recordCount <= 0){
                            // 읽음 정보 입력
                            insertReadUser(meetingId, userId);
                        } else {
                            updateRead(meetingId, userId, ci.get(ci.size() - 1).getMessage_seq());
                        }

                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    // 이미지를 전송할떄 사용하는 함수
    private void chattingImg(ChattingItem chattingItem, ArrayList<Uri> DataList){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/chat/chatImage.php").newBuilder();
        String url = urlBuilder.build().toString();

        RequestBody requestBody;

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("meeting_seq", String.valueOf(chattingItem.getMeeting_seq()))
                .addFormDataPart("user_seq", String.valueOf(chattingItem.getUser_seq()))
                .addFormDataPart("message_read", String.valueOf(ui.size() + 1))
                .addFormDataPart("message_date", chattingItem.getMessage_date());

        for (int i = 0; i < DataList.size(); i++) {
            Uri imageUri = DataList.get(i);

            String imageUriString = imageUri.toString();

            String filePath;
            // imageUri가 'file://'로 시작하는 경우
            if (imageUriString.startsWith("file://")) {
                // 'file://'을 제거하여 실제 파일 경로를 얻음
                filePath = imageUriString.substring(7);
            } else {
                filePath = FileUtils.getPathFromUri(getContext(), imageUri);
            }
            File file = new File(filePath);
            multipartBuilder.addFormDataPart("image" + i, "image"+i+".png", RequestBody.create(MediaType.parse("image/*"), file));
        }

        requestBody = multipartBuilder.build();

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
                    System.out.println("채팅 이미지 : " + responseData);
                    Gson gson = new Gson();
                    if(getActivity() != null && !getActivity().isFinishing()){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ChatImg[] images = gson.fromJson(responseData, ChatImg[].class);
                                for (ChatImg chatImg : images){
                                    ChattingItem ch = new ChattingItem();
                                    ch.setMessage_seq(chatImg.getImgSeq());
                                    ch.setMeeting_seq(chattingItem.getMeeting_seq());
                                    ch.setUser_seq(chattingItem.getUser_seq());
                                    ch.setMessage_content(chatImg.getImgUrl());
//                                System.out.println("이미지 아이디 : " + chatImg.getChatSeq());

                                    ch.setMessage_date(chattingItem.getMessage_date());
                                    ch.setUser_nickname(my.getUserNick());
                                    ch.setUser_url(my.getUserUrl());

                                    Map<String, Object> map = new HashMap<>();
                                    map.put("userSeq", ch.getUser_seq());
                                    map.put("action", "chat");
                                    map.put("meetingSeq", ch.getMeeting_seq());
                                    map.put("content", ch.getMessage_content());
                                    map.put("chatTime", ch.getMessage_date());
                                    map.put("seq", ch.getMessage_seq());
                                    String json = gson.toJson(map);

                                    Intent serviceIntent = new Intent(getContext(), ChattingSocketService.class);
                                    serviceIntent.putExtra("actionJson", json);
                                    getContext().startService(serviceIntent);

//                                    try {
//                                        Thread.sleep(300);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }

                                    ci.add(ch);
                                    System.out.println("ch message_seq : " + ch.getMessage_seq());
                                    updateRead(meetingId, userId, ch.getMessage_seq());
                                    chattingAdapter.notifyDataSetChanged();
                                    chatRecyclerView.scrollToPosition(chattingAdapter.getItemCount() - 1); // 리사이클려뷰 마지막 아이템이 보이게

                                }
                                System.out.println("ci message_seq : " + ci.get(ci.size() - 1).getMessage_seq());
                                updateRead(meetingId, userId, ci.get(ci.size() - 1).getMessage_seq());
                                chattingAdapter.notifyDataSetChanged();
                                chatRecyclerView.scrollToPosition(chattingAdapter.getItemCount() - 1); // 리사이클려뷰 마지막 아이템이 보이게
                            }
                        });
                    }
                }
            }
        });
    }

    // 채팅방의 입장한 사람들에게 이미지를 전송합니다.

    // 내가 채팅방에 입장했다는 정보를 소켓에 전송을 위한 json 작성
    private String enterChatRoom(int userId){
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("userSeq", userId);
        map.put("action", "enter");
        map.put("meetingSeq", meetingId);
        String jsonEnter = gson.toJson(map);
        return jsonEnter;
    }
    // 채팅의 읽음정보를 업데이트
    private void endUpdateRead(int meetingId, int userId){
        System.out.println("updateRead 실행");
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/chat/updateRead.php").newBuilder();
        urlBuilder.addQueryParameter("meeting_seq", String.valueOf(meetingId)); // url 쿼리에 id 라는 메개변수 추가
        urlBuilder.addQueryParameter("user_seq", String.valueOf(userId));
        urlBuilder.addQueryParameter("read_time", getCurrentDateTime());
        urlBuilder.addQueryParameter("chat_seq", String.valueOf(ci.get(ci.size() - 1).getMessage_seq()));
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
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    System.out.println("업데이트 완료");
                    System.out.println(responseData);
                }
            }
        });
    }

    private void updateChattingRead() {
        System.out.println("updateChatting 실행");

        for (int i = 0; i < ci.size(); i++) {
            ci.get(i).setMessage_read(ui.size() + 1);
        }

        for (int i = 0; i < ci.size(); i++) {
            for (int j = 0; j < ri.size(); j++) {
                if(ci.get(i).getMessage_seq() <= ri.get(j).getChat_seq()){ // 채팅 고유 아이디를 비교
                    ci.get(i).setMessage_read(ci.get(i).getMessage_read() - 1);
                }
            }
        }

//        System.out.println("이미지 아이디 : " + ci.get(ci.size() - 1).getMessage_seq());
//        for (int i = 0; i < ri.size(); i++) {
//            System.out.println("읽음 유저 : " + ri.get(i).getUser_seq());
//            System.out.println("마지막 읽은 정보 : " + ri.get(i).getChat_seq());
//        }

        chattingAdapter.notifyDataSetChanged();
        chatRecyclerView.scrollToPosition(chattingAdapter.getItemCount() - 1); // 리사이클려뷰 마지막 아이템이 보이게
    }

    // 채팅의 읽음정보를 업데이트
    private void updateRead(int meetingId, int userId, int messageSeq){
        System.out.println("updateRead 실행");
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/chat/updateRead.php").newBuilder();
        urlBuilder.addQueryParameter("meeting_seq", String.valueOf(meetingId)); // url 쿼리에 id 라는 메개변수 추가
        urlBuilder.addQueryParameter("user_seq", String.valueOf(userId));
        urlBuilder.addQueryParameter("read_time", getCurrentDateTime());
        urlBuilder.addQueryParameter("chat_seq", String.valueOf(messageSeq));
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
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    System.out.println("업데이트 완료");
                    System.out.println(responseData);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReadUserList(meetingId);
                        }
                    });
                }
            }
        });
    }
    // 채팅 입력
    private void inputChatting(ChattingItem chattingItem) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/chat/inputChatting.php").newBuilder();
        String url = urlBuilder.build().toString();
        Gson gson = new Gson();

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("meeting_seq", String.valueOf(chattingItem.getMeeting_seq()))
                .add("user_seq", String.valueOf(chattingItem.getUser_seq()))
                .add("message_content", chattingItem.getMessage_content())
                .add("message_read", String.valueOf(ui.size() + 1))
                .add("message_date", chattingItem.getMessage_date())
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    System.out.println("채팅 입력 완료");
                    System.out.println(responseData);
                    if (getActivity() != null && !getActivity().isFinishing()){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Map<String, Object> map = new HashMap<>();
                                map.put("userSeq", userId);
                                map.put("action", "chat");
                                map.put("meetingSeq", meetingId);
                                map.put("chatTime", chattingItem.getMessage_date());
                                map.put("content", chattingItem.getMessage_content());
                                map.put("seq", Integer.parseInt(responseData));
                                // 채팅 내용을 json 형태로 가공
                                String json = gson.toJson(map);

                                // 소켓 통신을 담당하는 서비스로 json 을 전송
                                Intent serviceIntent = new Intent(getContext(), ChattingSocketService.class);
                                serviceIntent.putExtra("actionJson", json);
                                getContext().startService(serviceIntent);

                                chattingItem.setMessage_seq(Integer.parseInt(responseData));

                                updateRead(meetingId, userId, Integer.parseInt(responseData));

                                int newPosition = ci.size();
                                ci.add(chattingItem);
                                chattingAdapter.notifyItemInserted(newPosition);
                                chatRecyclerView.scrollToPosition(chattingAdapter.getItemCount() - 1); // 리사이클러뷰 마지막 아이템이 보이게
                            }
                        });
                    }
                }
            }
        });
    }

    // 채팅 읽은 유저 정보 가져오기
    private void getReadUserList(int meetingId){
        System.out.println("getReadUserList 실행");
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/chat/getReadList.php").newBuilder();
        urlBuilder.addQueryParameter("meeting_seq", String.valueOf(meetingId)); // url 쿼리에 id 라는 메개변수 추가
        String url = urlBuilder.build().toString();
        JsonToData jt = new JsonToData();

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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ri.clear();
                            ri.addAll(jt.jsonToReadList(responseData));
                            updateChattingRead();
                        }
                    });
                }
            }
        });
    }
    // 현재 시간 입력
    private void insertReadUser(int meetingId, int userId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/chat/inputRead.php").newBuilder();
        String url = urlBuilder.build().toString();

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("meeting_seq", String.valueOf(meetingId))
                .add("user_seq", String.valueOf(userId))
                .add("read_time", getCurrentDateTime())
                .add("chat_seq", String.valueOf(ci.get(ci.size() - 1).getMessage_seq()))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    System.out.println("입력 완료");
                    System.out.println(responseData);
                    if (getActivity() != null && !getActivity().isFinishing()){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getReadUserList(meetingId);
                            }
                        });
                    }
                }
            }
        });
    }

    // 채팅 리스트 가져오기
    private void getChattingList(int meetingId) {
        System.out.println("getChattingList 실행");
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/chat/getChattingList.php").newBuilder();
        urlBuilder.addQueryParameter("meetingId", String.valueOf(meetingId)); // url 쿼리에 id 라는 메개변수 추가
        String url = urlBuilder.build().toString();
        JsonToData jt = new JsonToData();

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
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    System.out.println(responseData);
                    if ("[]".equals(responseData)) { // chatting 내역이 없을때
                        System.out.println("null 입니다.");
                    } else { // chatting 내역이 잇을때
                        if (getActivity() != null && !getActivity().isFinishing()) { // 엑티비티 로딩 끝났을떄
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ci.clear();
                                    ci.addAll(jt.jsonToChattingList(responseData));

                                    getReadUser(meetingId, userId);
                                }
                            });
                        }
                    }
                } // end response.isSuccessful()
            }
        });
    } // end getChattingList

    // 채팅방 유저의 닉네임과 url 가져옴
    private void getUsersInfo(int meetingId, int userId) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/chat/getUsersInfo.php").newBuilder();
        urlBuilder.addQueryParameter("meetingId", String.valueOf(meetingId)); // url 쿼리에 id 라는 메개변수 추가
        String url = urlBuilder.build().toString();
        JsonToData jt = new JsonToData();

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
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    System.out.println(responseData);
                    ui.clear();
                    ui.addAll(jt.jsonUserList(responseData));
                    for (int i = 0; i < ui.size(); i++) {
                        if (ui.get(i).getUserSeq() == userId) {
                            my.setUserUrl(ui.get(i).getUserUrl());
                            my.setUserNick(ui.get(i).getUserNick());
                            ui.remove(i);
                            break;
                        }
                    }
                }
            }
        });
    }

    // 현재 시간을 가져오기
    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 현재 시간을 가져옴
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }
}