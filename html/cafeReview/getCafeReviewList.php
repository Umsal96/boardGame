<?php

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $cafeId = $_GET['cafeId'];
    $reviewType = 1;

    $stmt = $conn->prepare("SELECT review_table.*, user_info.user_url, user_info.user_nickname,
    GROUP_CONCAT(image_to_table.image_seq) AS to_seqs, 
    GROUP_CONCAT(image_table.image_url) AS image_urls
    FROM review_table
    JOIN user_info ON review_table.user_seq = user_info.user_seq
    LEFT JOIN image_to_table ON review_table.review_seq = image_to_table.review_seq
    LEFT JOIN image_table ON image_to_table.image_seq = image_table.image_seq
    WHERE review_table.cafe_seq = :cafe_seq AND review_table.review_type = :review_type
    GROUP BY review_table.review_seq
    ORDER BY review_table.review_seq DESC");

    // 쿼리에 바인딩
    $stmt->bindParam(':cafe_seq', $cafeId, PDO::PARAM_INT);
    $stmt->bindParam(':review_type', $reviewType, PDO::PARAM_INT);

    // 쿼리 실행
    $stmt->execute();

    //결과를 받음
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // 결과를 json 형식으로 변환
    $jsonResult = json_encode($result);

    echo $jsonResult;
?>