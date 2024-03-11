<?php 

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $userId = $_GET['userId'];

    $stmt = $conn->prepare("SELECT meeting_seq FROM member_table 
        WHERE user_seq = :user_seq");

    $stmt->bindParam(':user_seq', $userId, PDO::PARAM_INT);
    $stmt->execute();

    $meetingSeqs = $stmt->fetchAll(PDO::FETCH_COLUMN);

    // JSON 형식으로 반환
    echo json_encode($meetingSeqs);

?>