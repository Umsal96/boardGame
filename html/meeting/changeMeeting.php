<?php

    // 모임장을 바꾸는 php
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    $leaderId  = $_GET['leaderId']; // 방장의 고유 아이디
    $userId = $_GET['userId']; // 유저의 고유 아이디
    $meetingId = $_GET['meetingId']; // 모임의 고유 아이디

    // 일반 유저를 방장으로 만듬
    $stmt = $conn->prepare("UPDATE member_table
        SET member_leader = 1
        WHERE meeting_seq = :meeting_seq AND user_seq = :user_seq");
    
    $stmt->bindParam(':meeting_seq', $meetingId, PDO::PARAM_INT);
    $stmt->bindParam(':user_seq', $userId, PDO::PARAM_INT);
    $stmt->execute();

    // 유저를 방장으로 만듬
    $stmt1 = $conn->prepare("UPDATE member_table
        SET member_leader = 0
        WHERE meeting_seq = :meeting_seq AND user_seq = :user_seq");

    $stmt1->bindParam(':meeting_seq', $meetingId, PDO::PARAM_INT);
    $stmt1->bindParam(':user_seq', $leaderId, PDO::PARAM_INT);
    $stmt1->execute();

    echo "수정 완료";
?>