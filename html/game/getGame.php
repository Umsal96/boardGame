<?php 

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결 
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $game_seq = $_GET['gameId'];

    $stmt = $conn->prepare("SELECT game_table.*,
    GROUP_CONCAT(image_to_table.image_seq) AS to_seqs, 
    GROUP_CONCAT(image_table.image_url) AS image_urls,
    AVG(COALESCE(review_table.review_grade, NULL)) AS average_review_grade
    FROM game_table
    LEFT JOIN review_table ON game_table.game_seq = review_table.game_seq
    LEFT JOIN image_to_table ON game_table.game_seq = image_to_table.game_seq
    LEFT JOIN image_table ON image_to_table.image_seq = image_table.image_seq
    WHERE game_table.game_seq = :game_seq
    ORDER BY image_table.image_order");

    // 쿼리 바인딩
    $stmt->bindParam(':game_seq', $game_seq, PDO::PARAM_INT);

    // 쿼리 실행
    $stmt->execute();

    $result = $stmt->fetch(PDO::FETCH_ASSOC);

    $game_seq = $result['game_seq'];
    $game_name = $result['game_name'];
    $game_summary = $result['game_summary'];
    $game_min = $result['game_min'];
    $game_max = $result['game_max'];
    $game_detail = $result['game_detail'];
    $game_create_data = $result['game_create_data'];
    $image_urls  = $result['image_urls'];
    $to_seqs = $result['to_seqs'];
    $average_review_grade = $result['average_review_grade'];

    $data = [
        'game_seq' => $game_seq,
        'game_name' => $game_name,
        'game_summary' => $game_summary,
        'game_min' => $game_min,
        'game_max' => $game_max,
        'game_detail' => $game_detail,
        'game_create_data' => $game_create_data,
        'image_urls' => $image_urls,
        'to_seqs' => $to_seqs,
        'average_review_grade' => $average_review_grade
    ];

    echo json_encode($data);
?>