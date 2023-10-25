<?php 

    // 오류가 발생시 출력됨
    error_reporting(E_ALL); // 모든 php 오류를 표시하도록 설정합니다.
    ini_set('display_errors', '1'); // 오류를 화면에 표시하도록 설정합니다.

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $meeting = $_GET['meeting']; // 모임 고유 아이디
    $schedule = $_GET['schedule']; // 일정 고유 아이디
    $userId = $_GET['userId']; // 유저의 고유 아이디

    $stmt = $conn->prepare("INSERT INTO schedule_member(schedule_seq, user_seq, into_schedule, meeting_seq)
        VALUES(:schedule_seq, :user_seq, NOW(), :meeting_seq)");

    $stmt->bindParam(':schedule_seq', $schedule, PDO::PARAM_INT);
    $stmt->bindParam(':user_seq', $userId, PDO::PARAM_INT);
    $stmt->bindParam(':meeting_seq', $meeting, PDO::PARAM_INT);
    $stmt->execute();

    $stmt1 = $conn->prepare("UPDATE meeting_schedule_table 
        SET schedule_member_current = schedule_member_current + 1
        WHERE schedule_seq = :schedule_seq");
        
    $stmt1->bindParam(':schedule_seq', $schedule, PDO::PARAM_INT);
    $stmt1->execute();

    // 모임테이블에서 현재 인원수를 받아오는 쿼리
    $stmt2 = $conn->prepare("SELECT * FROM meeting_schedule_table 
        WHERE schedule_seq = :schedule_seq");

    $stmt2->bindParam(':schedule_seq', $schedule, PDO::PARAM_INT);
    $stmt2->execute();
    $result = $stmt2->fetch(PDO::FETCH_ASSOC);

    // 결과를 연관 배ㄹ에서 직접 추출
    // 일정 고유 아이디
    $schedule_seq = $result['schedule_seq'];
    // 유저 고유 아이디
    $user_seq = $result['user_seq'];
    // 모임 고유 아이디
    $meeting_seq = $result['meeting_seq'];
    // 일정 제목
    $schedule_title = $result['schedule_title'];
    // 일정 날짜
    $schedule_date = $result['schedule_date'];
    // 일정 시간
    $schedule_time = $result['schedule_time'];
    // 일정 참가 가능한 최대 인원수
    $schedule_member_max = $result['schedule_member_max'];
    // 일정 현재 인원수
    $schedule_member_current = $result['schedule_member_current'];
    // 일전 장소
    $schedule_place_name = $result['schedule_place_name'];
    // 일정 주소
    $schedule_place_address = $result['schedule_place_address'];
    // 일정 장소 좌표 y
    $schedule_lat = $result['schedule_lat'];
    // 일정 장소 좌표 x
    $schedule_lnt = $result['schedule_lnt'];
    // 일정 작성 날짜
    $schedule_create_date = $result['schedule_create_date'];
    // 일정 수정 날짜
    $schedule_modified_date = $result['schedule_modified_date'];

    $data = [
        'schedule_seq' => $schedule_seq,
        'user_seq' => $user_seq,
        'meeting_seq' => $meeting_seq,
        'schedule_title' => $schedule_title,
        'schedule_date' => $schedule_date,
        'schedule_time' => $schedule_time,
        'schedule_member_max' => $schedule_member_max,
        'schedule_member_current' => $schedule_member_current,
        'schedule_place_name' => $schedule_place_name,
        'schedule_place_address' => $schedule_place_address,
        'schedule_lat' => $schedule_lat,
        'schedule_lnt' => $schedule_lnt,
        'schedule_create_date' => $schedule_create_date,
        'schedule_modified_date' => $schedule_modified_date
    ];

    echo json_encode($data);
    
?>