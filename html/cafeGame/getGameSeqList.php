<?php 

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $cafeId = $_GET['cafeId'];

    $stmt = $conn->prepare("SELECT game_seq FROM cafe_game_table 
        WHERE cafe_seq = :cafe_seq");

    $stmt->bindParam(':cafe_seq', $cafeId, PDO::PARAM_INT);
    $stmt->execute();

    $imageSeqs = $stmt->fetchAll(PDO::FETCH_COLUMN);

    // JSON 형식으로 반환
    echo json_encode($imageSeqs);

?>