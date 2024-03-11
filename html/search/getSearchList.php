<?php 

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $user_seq = $_GET['user_seq'];

    $search_type = $_GET['search_type'];

    // 내가 입력했던 검색어를 가져오는 쿼리 작성
    $stmt = $conn->prepare("SELECT * FROM search_table
    WHERE user_seq = :user_seq AND search_type = :search_type
    ORDER BY search_create_date DESC");

    // 쿼리 바인딩
    $stmt->bindParam(':user_seq', $user_seq, PDO::PARAM_INT);
    $stmt->bindParam(':search_type', $search_type, PDO::PARAM_INT);

    // 쿼리 실행
    $stmt->execute();

    // 결과를 받아옴
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // 결과를 json 형식으로 변환
    $jsonResult = json_encode($result);

    echo $jsonResult;

?>