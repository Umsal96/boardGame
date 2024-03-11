<?php

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $foodId = $_GET['foodId'];

    $stmt = $conn->prepare("SELECT image_seq FROM image_to_table
        WHERE food_seq = :food_seq");
    
    $stmt->bindParam(':food_seq', $foodId, PDO::PARAM_INT);
    $stmt->execute();

    $imageSeq = $stmt->fetchColumn();

    if (empty($imageSeq)){
        // 주어진 foodId에 대한 이미지 시퀀스가 없을 겨우 처리
        echo '해당 foodId에 대한 이미지가 없습니다';

        $stmt1 = $conn->prepare("DELETE FROM cafe_food_table 
            WHERE food_seq = :food_seq");
        
        $stmt1->bindParam(':food_seq', $foodId, PDO::PARAM_INT);
        if ($stmt1->execute()) {
            echo '게시글 삭제 성공';
        } else {
            echo '게시글 삭제 실패';
        }
        exit;
    }

    $stmt2 = $conn->prepare("SELECT image_url FROM image_table
        WHERE image_seq = :image_seq");

    $stmt2->bindParam(':image_seq', $imageSeq, PDO::PARAM_INT);
    $stmt2->execute();

    $imageUrl = $stmt2->fetchColumn();

    $uploadDir = '/var/www/html';

    $imagePath = $uploadDir . $imageUrl;

    if (file_exists($imagePath) && unlink($imagePath)) {
        // 이미지 삭제 성공
        echo '이미지 삭제 성공: ' . $imageUrl;
    } else {
        echo '이미지 삭제 실패 또는 이미지가 존재하지 않음: ' . $imageUrl;
    }

    $stmt3 = $conn->prepare("DELETE FROM cafe_food_table 
            WHERE food_seq = :food_seq");
        
    $stmt3->bindParam(':food_seq', $foodId, PDO::PARAM_INT);
    if ($stmt3->execute()) {
        echo '게시글 삭제 성공';
    } else {
        echo '게시글 삭제 실패';
    }

?>