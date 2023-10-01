<?php

    // 오류가 발생시 출력됨
    error_reporting(E_ALL); // 모든 php 오류를 표시하도록 설정합니다.
    ini_set('display_errors', '1'); // 오류를 화며ㅐㄴ에 표시하도록 설정합니다.

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    $id = $_GET['id'];

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $stmt = $conn->prepare("SELECT * FROM meeting_schedule_table WHERE meeting_seq = :meeting_seq");
    $stmt->bindParam(':meeting_seq', $id, PDO::PARAM_INT);
    $stmt->execute();

    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($result);
?>