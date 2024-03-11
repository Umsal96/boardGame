<?php 

    // 가입한 유저의 정보를 가져옴
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    $cafeId = $_POST['cafeId'];
    $gameSeqArray = explode(',', $_POST['gameSeq']);

    foreach($gameSeqArray as $gameSeq){
        $stmt = $conn->prepare("INSERT INTO cafe_game_table(game_seq, cafe_seq, 
        cafe_game_create_date)
        VALUES (:game_seq, :cafe_seq, NOW())");
    
        $stmt->bindParam(':game_seq', $gameSeq, PDO::PARAM_INT);
        $stmt->bindParam(':cafe_seq', $cafeId, PDO::PARAM_INT);

        $stmt->execute();
    }

    echo '입력완료';
    
?>