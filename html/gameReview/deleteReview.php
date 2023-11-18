<?php 

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $reviewId = $_GET['reviewId'];

    $stmt = $conn->prepare("SELECT image_seq FROM image_to_table 
    WHERE review_seq = :review_seq");

    $stmt->bindParam(':review_seq', $reviewId, PDO::PARAM_INT);
    $stmt->execute();

    $imageSeqs = $stmt->fetchAll(PDO::FETCH_COLUMN);

    if (empty($imageSeqs)) {
        // 주어진 boardId에 대한 이미지 시퀀스가 없을 경우 처리
        echo '해당 boardId에 대한 이미지가 없습니다';

        // 게시글 삭제
        $stmtDeleteBoard = $conn->prepare("DELETE FROM review_table WHERE review_seq = :review_seq");
        $stmtDeleteBoard->bindParam(':review_seq', $reviewId, PDO::PARAM_INT);
        if ($stmtDeleteBoard->execute()) {
            echo '게시글 삭제 성공';
        } else {
            echo '게시글 삭제 실패';
        }

        exit;
    }

    // 두 번째 쿼리에서 IN 절 사용
    $placeholders = implode(',', array_fill(0, count($imageSeqs), '?'));

    $stmt1 = $conn->prepare("SELECT image_url FROM image_table
        WHERE image_seq IN ($placeholders)");

    // 각 image_seq 값을 바인딩
    foreach ($imageSeqs as $key => $imageSeq) {
        $stmt1->bindValue($key + 1, $imageSeq, PDO::PARAM_INT);
    }

    $stmt1->execute();

    $imageURIs = $stmt1->fetchAll(PDO::FETCH_COLUMN);

    if (!empty($imageURIs)) {
        // 이미지 URI가 존재하는 경우, 이미지 삭제 시도
        $uploadDir = '/var/www/html'; // 이미지가 저장된 디렉토리 경로

        foreach($imageSeqs as $imageSeq){
            $stmt2 = $conn->prepare("DELETE FROM image_table 
            WHERE image_seq = :image_seq");
        
            $stmt2->bindParam(':image_seq', $imageSeq, PDO::PARAM_INT);
            $stmt2->execute();
        }

        foreach ($imageURIs as $imageURI) {
            $imagePath = $uploadDir . $imageURI;
    
            if (file_exists($imagePath) && unlink($imagePath)) {
                // 이미지 삭제 성공
                echo '이미지 삭제 성공: ' . $imageURI;
            } else {
                echo '이미지 삭제 실패 또는 이미지가 존재하지 않음: ' . $imageURI;
            }
        }
    }

    // 게시글 삭제
    $stmtDeleteBoard = $conn->prepare("DELETE FROM review_table WHERE review_seq = :review_seq");
    $stmtDeleteBoard->bindParam(':review_seq', $reviewId, PDO::PARAM_INT);
    if ($stmtDeleteBoard->execute()) {
        echo '게시글 삭제 성공';
    } else {
        echo '게시글 삭제 실패';
    }
    

?>