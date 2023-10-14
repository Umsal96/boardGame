<?php 

    // 오류가 발생시 출력됨
    error_reporting(E_ALL); // 모든 php 오류를 표시하도록 설정합니다.
    ini_set('display_errors', '1'); // 오류를 화며ㅐㄴ에 표시하도록 설정합니다.

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

    echo "입력 성공";
?>