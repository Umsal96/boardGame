<?php 

    // 모임장 탈퇴 php
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    $meetingId = $_GET['meetingId']; // 미팅의 고유 아이디
    $userId = $_GET['userId']; // 유저의 고유 아이디

    // 쿼리 작성
    $stmt = $conn->prepare("DELETE FROM member_table WHERE 
        meeting_seq = :meeting_seq AND user_seq = :user_seq");

    $stmt->bindParam(':meeting_seq', $meetingId, PDO::PARAM_INT);
    $stmt->bindParam(':user_seq', $userId, PDO::PARAM_INT);

    $stmt->execute();

    $stmt1 = $conn->prepare("UPDATE meeting_table 
    SET meeting_current = meeting_current - 1
    WHERE meeting_seq = :meeting_seq");

    $stmt1->bindParam(':meeting_seq', $meetingId, PDO::PARAM_INT);
    $stmt1->execute();

    echo "삭제 성공"
?>