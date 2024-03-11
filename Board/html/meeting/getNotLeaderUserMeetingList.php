<?php 

    // 모임장 탈퇴 php
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    $meetingId = $_GET['meetingId']; // 미팅의 고유 아이디

    $stmt = $conn->prepare("SELECT member_table.member_leader, member_table.user_seq, user_info.user_nickname, user_info.user_url
    FROM member_table 
    JOIN user_info ON member_table.user_seq = user_info.user_seq
    WHERE member_table.meeting_seq = :meeting_seq AND member_table.member_leader = 0");

    $stmt->bindParam(':meeting_seq', $meetingId, PDO::PARAM_INT);

    // 쿼리 실행
    $stmt->execute();

    // 결과를 받아옴
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // 결과를 json 형식으로 변환
    $jsonResult = json_encode($result);

    echo $jsonResult;
?>