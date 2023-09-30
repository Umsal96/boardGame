<?php 

    // 오류가 발생시 출력됨
    error_reporting(E_ALL); // 모든 php 오류를 표시하도록 설정합니다.
    ini_set('display_errors', '1'); // 오류를 화며ㅐㄴ에 표시하도록 설정합니다.

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    $meeting_seq = $_POST['meeting_seq']; // 미팅 고유 아이디
    $user_seq = $_POST['user_seq']; // 일정 작성자 고유 아이디
    $schedule_title = $_POST['schedule_title']; // 일정 제목
    $schedule_date = $_POST['schedule_date']; // 일정 날짜
    $schedule_time = $_POST['schedule_time']; // 일정 시간
    $schedule_member_max = $_POST['schedule_member_max']; // 일정 인원
    $schedule_place_name = $_POST['schedule_place_name']; // 장소 이름
    $schedule_place_address = $_POST['schedule_place_address']; // 장소 주소
    $schedule_lat = $_POST['schedule_lat'];// 경도 
    $schedule_lnt = $_POST['schedule_lnt'];// 위도

    $stmt = $conn->prepare("INSERT INTO meeting_schedule_table(meeting_seq, user_seq, 
        schedule_title, schedule_date, schedule_time, schedule_member_max, schedule_place_name, 
        schedule_place_address, schedule_lat, schedule_lnt, schedule_create_date )
    VALUE(:meeting_seq, :user_seq, :schedule_title, :schedule_date, :schedule_time,
        :schedule_member_max, :schedule_place_name, :schedule_place_address, :schedule_lat, 
        :schedule_lnt, NOW())");

    // 메개변수 바인딩
    $stmt->bindParam(':meeting_seq', $meeting_seq);
    $stmt->bindParam(':user_seq', $user_seq);
    $stmt->bindParam(':schedule_title', $schedule_title);
    $stmt->bindParam(':schedule_date', $schedule_date);
    $stmt->bindParam(':schedule_time', $schedule_time);
    $stmt->bindParam(':schedule_member_max', $schedule_member_max);
    $stmt->bindParam(':schedule_place_name', $schedule_place_name);
    $stmt->bindParam(':schedule_place_address', $schedule_place_address);
    $stmt->bindParam(':schedule_lat', $schedule_lat);
    $stmt->bindParam(':schedule_lnt', $schedule_lnt);

    $stmt->execute();

    $stmt->closeCursor();
    $conn = null;
?>