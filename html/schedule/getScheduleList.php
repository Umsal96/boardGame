<?php

    // 오류가 발생시 출력됨
    error_reporting(E_ALL); // 모든 php 오류를 표시하도록 설정합니다.
    ini_set('display_errors', '1'); // 오류를 화며ㅐㄴ에 표시하도록 설정합니다.

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $id = $_GET['id']; // 받아온 모임 고유 아이디를 변수에 넣음

    // 현재 날짜와 시간을 구합니다
    $currentDateTime = new DateTime();

    // 30분을 추가한 시간을 계산합니다.
    $currentDateTime->add(new DateInterval('PT30M'));

    // 현재 날짜와 시간을 문자열로 변환합니다.
    $currentDateTimeStr = $currentDateTime->format('Y-m-d H:i:s');

    // 날짜와 시간을 합처서 현 시간을 비교하는 쿼리
    $stmt = $conn->prepare("SELECT * FROM meeting_schedule_table 
        WHERE meeting_seq = :meeting_seq AND CONCAT(schedule_date, ' ', schedule_time) > :current_datetime");
    $stmt->bindParam(':meeting_seq', $id, PDO::PARAM_INT);
    $stmt->bindParam(':current_datetime', $currentDateTimeStr, PDO::PARAM_STR);
    $stmt->execute();

    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($result);
?>