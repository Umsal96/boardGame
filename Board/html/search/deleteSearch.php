<?php

    // 오류가 발생시 출력됨
    error_reporting(E_ALL); // 모든 php 오류를 표시하도록 설정합니다.
    ini_set('display_errors', '1'); // 오류를 화며ㅐㄴ에 표시하도록 설정합니다.

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $searchSeq = $_GET['searchSeq'];

    $stmt = $conn->prepare("DELETE FROM search_table WHERE search_seq = :search_seq");

    $stmt->bindParam(':search_seq', $searchSeq, PDO::PARAM_INT);
    
    if($stmt->execute()){
        echo "삭제 성공";
    }else {
        echo "삭제 실패";
    }

?>