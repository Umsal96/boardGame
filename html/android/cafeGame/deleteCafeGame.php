<?php 

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $getCafeGameSeq = $_GET['getCafeGameSeq'];

    $stmt = $conn->prepare("DELETE FROM cafe_game_table
        WHERE cafe_game_seq = :cafe_game_seq");

    $stmt->bindParam(':cafe_game_seq', $getCafeGameSeq, PDO::PARAM_INT);
    if($stmt->execute()){
        echo '삭제 성공';
    }else {
        echo '삭제 실패';
    }
    

?>