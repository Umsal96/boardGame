<?php 

    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    $scheduleId = $_GET['scheduleId']; // 일정 고유 아이디

    $stmt = $conn->prepare("SELECT schedule_member.*, user_info.user_nickname, user_info.user_url
        FROM schedule_member
        JOIN user_info ON schedule_member.user_seq = user_info.user_seq
        WHERE schedule_member.schedule_seq = :schedule_seq");

    $stmt->bindParam(':schedule_seq', $scheduleId, PDO::PARAM_INT);
    
    $stmt->execute();

    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($result);
?>