<?php 

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $review_seq = $_GET['reviewId'];

    $stmt = $conn->prepare("SELECT review_table.*,
        GROUP_CONCAT(image_to_table.image_seq) AS to_seqs, 
        GROUP_CONCAT(image_table.image_url) AS image_urls
        FROM review_table
        LEFT JOIN image_to_table ON review_table.review_seq = image_to_table.review_seq
        LEFT JOIN image_table ON image_to_table.image_seq = image_table.image_seq
        WHERE review_table.review_seq = :review_seq
        ORDER BY image_table.image_order");

    // 쿼리에 바인딩

    $stmt->bindParam(':review_seq', $review_seq, PDO::PARAM_INT);

    // 쿼리 실행
    $stmt->execute();

    $result = $stmt->fetch(PDO::FETCH_ASSOC);

    $review_seq = $result['review_seq'];
    $user_seq = $result['user_seq'];
    $cafe_seq = $result['cafe_seq'];
    $review_content = $result['review_content'];
    $review_grade = $result['review_grade'];
    $review_type = $result['review_type'];
    $review_create_date = $result['review_create_date'];
    $to_seqs = $result['to_seqs'];
    $image_urls = $result['image_urls'];

    $data = [
        'review_seq' => $review_seq,
        'user_seq' => $user_seq,
        'cafe_seq' => $cafe_seq,
        'review_content' => $review_content,
        'review_grade' => $review_grade,
        'review_type' => $review_type,
        'review_create_date' => $review_create_date,
        'to_seqs' => $to_seqs,
        'image_urls' => $image_urls
    ];

    echo json_encode($data);
?>