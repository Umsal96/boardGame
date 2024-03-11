package com.example.boardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

/*
* 1. 버튼 및 체크박스를 맴버 변수로 선언
* 2. 버튼 및 체크박스의 변수와 ui 를 연결
* 3. 체크박스 확인용 메서드를 오버라이딩해서 제작
* 4. 2번과 3번의 체크박스를 등록함
* 5. 1번 체크박스 선택시 실행되는 이벤트 작성
* 6. 1번 체크박스의 이벤트에는 2번 3번의 체크박스가 모두 체크가 되어있는지 확인 하고 확인 되면 1번 체크박스 체크 되는것 추가
* 7. 1번 체크박스의 이벤트에 2번 3번 둘중 하나의 체크박스라도 체크가 해제되면 1번 체크박스가 해제되는 코드 추가
* 8. 2번 체크박스와 3번 체크박스가 둘다 체크되면 1번 체크박스가 체크되는거 추가
* 8. 2번 체크박스와 3번 체크박스 둘중 하나라도 체크가 안되어있다면
* */

public class ViewTerms extends AppCompatActivity {

    private CheckBox checkBox; // 약관 모두 동의
    private CheckBox checkBox2; // 서비스 이용약관
    private CheckBox checkBox3; // 개인정보 처리방침 이용약관 체크
    private Button button2; // 회원정보를 입력하는 페이지로 이동

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_terms);

        // ui 연결
        checkBox = findViewById(R.id.checkBox); // 약관 모두 동의
        checkBox2 = findViewById(R.id.checkBox2); // 서비스 이용 약관
        checkBox3 = findViewById(R.id.checkBox3); // 개인정보 처리 방침 이용약관 체크
        button2 = findViewById(R.id.button2); // sms 인증 페이지로 이동하는 버튼

        // 버튼의 배경색을 회색으로 설정 (비활성화 상태에서의 색상)
        int grayButton = Color.parseColor("#CCCCCC");
        button2.setBackgroundColor(grayButton); // sms 인증 페이지로 가는 버튼의 색을 회색으로 설정

        // 체크박스 상태가 변경될 때마다 체크 상태를 확인하고 버튼을 활성화 또는 비활성화
        checkBox2.setOnCheckedChangeListener(checkBoxListener); // 서비스 이용약관 체크박스가 변경되었을때 실행되는 함수
        checkBox3.setOnCheckedChangeListener(checkBoxListener); // 개인정보 처리 방침 이용약관 체크박스 변경되었을때 실행되는 함수

        // 약관 모두 동의 버튼을 눌렀을때  실행되는 이벤트
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){ // 만약 약관 모두 동의 버튼을 눌렀을때
                    checkBox2.setChecked(true); // 서비스 이용약관 체크 박스를 체크
                    checkBox3.setChecked(true); // 개인정보 처리 방침 이용약관 체크 박스 체크
                } else {
                    checkBox2.setChecked(false); // 서비스 이용약관 체크박스 체크 해제
                    checkBox3.setChecked(false); // 개인 정보 처리 방침 체크박스 체크 해제
                }
            }
        });

        // sms 인증 페이지로 이동하는 버튼을 클릭햇을때 실행되는 이벤트
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 인텐트에 엑티비티를 이동하는 의도를 담는다 ViewTerms 페이지에서 SMSAuth 페이지로 이동
                Intent intent = new Intent(ViewTerms.this, SMSAuth.class);
                startActivity(intent); // 인텐트를 실행
            }
        });
    }


    // check박스의 상태 (체크가 되어 있는지 아닌지) 변환 이벤트의 메소드 오버라이딩함
    private CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // 두개 다 체크가 되어있을 경우 버튼을 활성화
            if(checkBox2.isChecked() && checkBox3.isChecked()){
                button2.setEnabled(true); // sms 페이지로 가는 버튼 활성화
                int skyBlueButton = Color.parseColor("#3498DB"); // 하늘색 색상코드를 변수에 넣음
                button2.setBackgroundColor(skyBlueButton); // sms 페이지로 가는 버튼 배경색을 파란색으로 변경
                checkBox.setChecked(true); // 약관 모두 동의를 체크 상태로 변경
            } else { // 둘중 하나라도 체크가 안되어 있을경우 버튼을 비활성화
                button2.setEnabled(false); // sms 페이지로 가는 버튼 비활성화
                int grayButton = Color.parseColor("#CCCCCC");  // 회색 색상코드를 변수에 넣음
                button2.setBackgroundColor(grayButton); // sms 페이지로 가능 버튼 배경색 회색으로 변경
                checkBox.setChecked(false); // 약관 모두 동의를 체크 안함 상태로 변경
            }
        }
    };
}