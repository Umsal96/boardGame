<?php

    // 오류가 발생시 출력됨
    error_reporting(E_ALL); // 모든 php 오류를 표시하도록 설정합니다.
    ini_set('display_errors', '1'); // 오류를 화며ㅐㄴ에 표시하도록 설정합니다.

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    // 유저의 고유 아이디
    $user_seq = $_GET['userId'];

    // 미팅의 고유 아이디
    $meeting_seq = $_GET['meetingId'];

    $stmt = $conn->prepare("DELETE FROM member_wait 
    WHERE user_seq = :user_seq AND meeting_seq = :meeting_seq");

    $stmt->bindParam(':user_seq', $user_seq, PDO::PARAM_INT);
    $stmt->bindParam(':meeting_seq', $meeting_seq, PDO::PARAM_INT);

    $stmt->execute();
    
    echo "삭제 성공"

?>