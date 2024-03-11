<?php

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결 
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $cafe_seq = $_GET['cafeId'];

    $stmt = $conn->prepare("SELECT cafe_table.*,
    GROUP_CONCAT(image_to_table.image_seq) AS to_seqs, 
    GROUP_CONCAT(image_table.image_url) AS image_urls,
    AVG(COALESCE(review_table.review_grade, NULL)) AS average_review_grade
    FROM cafe_table
    LEFT JOIN review_table ON cafe_table.cafe_seq = review_table.cafe_seq
    LEFT JOIN image_to_table ON cafe_table.cafe_seq = image_to_table.cafe_seq
    LEFT JOIN image_table ON image_to_table.image_seq = image_table.image_seq
    WHERE cafe_table.cafe_seq = :cafe_seq
    ORDER BY image_table.image_order");

    // 쿼리 바인딩
    $stmt->bindParam(':cafe_seq', $cafe_seq, PDO::PARAM_INT);

    // 쿼리 실행
    $stmt->execute();

    $result = $stmt->fetch(PDO::FETCH_ASSOC);

    $cafe_seq = $result['cafe_seq'];
    $cafe_name = $result['cafe_name'];
    $cafe_content = $result['cafe_content'];
    $cafe_create_date = $result['cafe_create_date'];
    $cafe_lat = $result['cafe_lat'];
    $cafe_lnt = $result['cafe_lnt'];
    $cafe_address = $result['cafe_address'];
    $to_seqs = $result['to_seqs'];
    $image_urls = $result['image_urls'];
    $average_review_grade = $result['average_review_grade'];

    $data = [
        'cafe_seq' => $cafe_seq,
        'cafe_name' => $cafe_name,
        'cafe_content' => $cafe_content,
        'cafe_create_date' => $cafe_create_date,
        'cafe_lat' => $cafe_lat,
        'cafe_lnt' => $cafe_lnt,
        'cafe_address' => $cafe_address,
        'to_seqs' => $to_seqs,
        'image_urls' => $image_urls,
        'average_review_grade' => $average_review_grade
    ];

    echo json_encode($data);
?>