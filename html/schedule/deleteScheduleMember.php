<?php 

    // 오류가 발생시 출력됨
    error_reporting(E_ALL); // 모든 php 오류를 표시하도록 설정합니다.
    ini_set('display_errors', '1'); // 오류를 화며ㅐㄴ에 표시하도록 설정합니다.

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $scheduleId = $_GET['schedule']; // 일정의 고유 아이디
    $userId = $_GET['userId']; // 유저의 고유 아이디

    $stmt = $conn->prepare("DELETE FROM schedule_member WHERE 
        schedule_seq = :schedule_seq AND user_seq = :user_seq");
    
    $stmt->bindParam(':schedule_seq', $scheduleId, PDO::PARAM_INT);
    $stmt->bindParam(':user_seq', $userId, PDO::PARAM_INT);
    $stmt->execute();

    $stmt1 = $conn->prepare("UPDATE meeting_schedule_table 
        SET schedule_member_current = schedule_member_current - 1
        WHERE schedule_seq = :schedule_seq");
        
    $stmt1->bindParam(':schedule_seq', $scheduleId, PDO::PARAM_INT);
    $stmt1->execute();

    echo "삭제 성공";

?>