<?php 

    // 오류가 발생시 출력됨
    error_reporting(E_ALL); // 모든 php 오류를 표시하도록 설정합니다.
    ini_set('display_errors', '1'); // 오류를 화며ㅐㄴ에 표시하도록 설정합니다.

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $user_seq = $_GET['user_seq'];
    $search_type = $_GET['type'];

    $stmt = $conn->prepare("DELETE FROM search_table 
        WHERE user_seq = :user_seq AND search_type = :search_type");

    $stmt->bindParam(':user_seq', $user_seq, PDO::PARAM_INT);
    $stmt->bindParam(':search_type', $search_type, PDO::PARAM_INT);

    if($stmt->execute()){
        echo "삭제 성공";
    }else {
        echo "삭제 실패";
    }
?>