<?php 

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $cafeId = $_GET['cafeId'];

    $sql = "SELECT cafe_game_table.*, game_table.game_name,
    (SELECT image_seq FROM image_to_table WHERE image_to_table.game_seq = cafe_game_table.game_seq LIMIT 1) AS image_seq,
    (SELECT image_url FROM image_table WHERE image_table.image_seq = (
        SELECT image_seq 
        FROM image_to_table 
        WHERE image_to_table.game_seq = cafe_game_table.game_seq 
        LIMIT 1
    )
    ) AS image_url
FROM cafe_game_table
    JOIN game_table ON cafe_game_table.game_seq = game_table.game_seq
WHERE 
    cafe_game_table.cafe_seq = :cafe_seq  -- Replace :your_game_seq with the specific game_seq you're interested in
GROUP BY 
    cafe_game_table.cafe_game_seq, image_seq, image_url
ORDER BY 
    cafe_game_table.cafe_game_seq DESC";

    $stmt = $conn->prepare($sql);

    $stmt->bindParam(':cafe_seq', $cafeId, PDO::PARAM_INT);

    // 쿼리 실행
    $stmt->execute();

    //결과를 받음
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // 결과를 json 형식으로 변환
    $jsonResult = json_encode($result);

    echo $jsonResult;

?>