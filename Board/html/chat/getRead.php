<?php 

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $meeting_seq = $_GET['meeting_seq'];
    $user_seq = $_GET['user_seq'];

    $stmt = $conn->prepare("SELECT * FROM read_table
    WHERE meeting_seq = :meeting_seq AND user_seq = :user_seq");
    
    // 쿼리에 바인딩
    $stmt->bindParam(':meeting_seq', $meeting_seq, PDO::PARAM_INT);
    $stmt->bindParam(':user_seq', $user_seq, PDO::PARAM_INT);

    // 쿼리 실행
    $stmt->execute();

    // 결과 레코드 갯수 얻기
    $rowCount = $stmt->rowCount();

    $data = [
        'record_count' => $rowCount,
    ];

    echo json_encode($data);
?>