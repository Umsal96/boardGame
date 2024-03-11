<?php

// 오류가 발생시 출력됨
    error_reporting(E_ALL); // 모든 php 오류를 표시하도록 설정합니다.
    ini_set('display_errors', '1'); // 오류를 화면에 표시하도록 설정합니다.

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $meetingId = $_GET['id']; // 모임의 고유 아이디

    $stmt = $conn->prepare("SELECT member_wait.user_seq, member_wait.meeting_seq
        , user_info.user_nickname, user_info.user_url
        FROM member_wait
        JOIN user_info ON member_wait.user_seq = user_info.user_seq
        WHERE member_wait.meeting_seq = :meeting_seq");

    // 쿼리에 바인딩
    $stmt->bindParam(':meeting_seq', $meetingId, PDO::PARAM_INT);
    
    // 쿼리 실행
    $stmt->execute();

    // 결과 받아옴
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // 결과를 json 형식으로 변환
    $jsonResult = json_encode($result);

    echo $jsonResult;
?>