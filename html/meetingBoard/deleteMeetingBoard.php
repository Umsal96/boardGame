<?php 

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $boardId = $_GET['boardId'];

    $stmt = $conn->prepare("SELECT image_url FROM image_table 
    WHERE board_seq = :board_seq");

    $stmt->bindParam(':board_seq', $boardId, PDO::PARAM_INT);
    $stmt->execute();

    $imageURIs = $stmt->fetchAll(PDO::FETCH_COLUMN);

    if (!empty($imageURIs)) {
        // 이미지 URI가 존재하는 경우, 이미지 삭제 시도
        $uploadDir = '/var/www/html'; // 이미지가 저장된 디렉토리 경로
    
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
    $stmtDeleteBoard = $conn->prepare("DELETE FROM meeting_board WHERE board_seq = :board_seq");
    $stmtDeleteBoard->bindParam(':board_seq', $boardId, PDO::PARAM_INT);
    if ($stmtDeleteBoard->execute()) {
        echo '게시글 삭제 성공';
    } else {
        echo '게시글 삭제 실패';
    }

?>