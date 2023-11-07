<?php 

    // 오류가 발생시 출력됨
    error_reporting(E_ALL); // 모든 php 오류를 표시하도록 설정합니다.
    ini_set('display_errors', '1'); // 오류를 화면에 표시하도록 설정합니다.

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    $boardId = $_GET['boardId'];
    $order = $_GET['imageOrder'];

    $stmt = $conn->prepare("SELECT image_url FROM image_table 
        WHERE board_seq = :board_seq AND image_order = :image_order");
    
    $stmt->bindParam(':board_seq', $boardId, PDO::PARAM_INT);
    $stmt->bindParam(':image_order', $order, PDO::PARAM_INT);
    $stmt->execute();

    $previousImage = $stmt->fetchColumn();

    $uploadDir = '/var/www/html';

    echo $uploadDir . $previousImage;

    if (file_exists($uploadDir . $previousImage)) {
        if (unlink($uploadDir . $previousImage)) {
            echo '파일 삭제 성공';

            $stmt1 = $conn->prepare("DELETE FROM image_table 
            WHERE board_seq = :board_seq AND image_order = :image_order ");

            $stmt1->bindParam(':board_seq', $boardId, PDO::PARAM_INT);
            $stmt1->bindParam(':image_order', $order, PDO::PARAM_INT);
            $stmt1->execute();

            echo 'db 삭제 성공';

        } else {
            echo '삭제 실패';
        }
    } else {
        echo '파일이 존재하지 않음';
    }
    

    
?>