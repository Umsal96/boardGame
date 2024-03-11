<?php

    // 데이터 베이스 연결
    include './db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    // 전송한 유저의 고유 아이디를 받음
    $Suser_id = $_POST['user_id'];

    // 아이디를 정수형으로 변경
    $user_id = (int) $Suser_id;

    // 쿼리문 작성 user_info 테이블에서 user_url쿼리를 가져옴
    $stmt = $conn->prepare("SELECT user_url FROM user_info WHERE user_seq = :user_seq");
    $stmt->bindParam(':user_seq', $user_id, PDO::PARAM_INT);
    $stmt->execute();
    $previousImage = $stmt->fetchColumn();

    $uploadDir = '/var/www/html';

    $uploadDBDir = '';
 
    // 이전 이미지 삭제
    if ($previousImage && file_exists($uploadDir . $previousImage)) {
        if(unlink($uploadDir . $previousImage)){
            // 쿼리문 작성 user_url 과 user_modified_date 를 수정
            $stmt2 = $conn->prepare("UPDATE user_info SET user_url = :user_url, user_modified_date = NOW() WHERE user_seq = :user_seq");
            $stmt2->bindParam(':user_url', $uploadDBDir, PDO::PARAM_STR);
            $stmt2->bindParam(':user_seq', $user_id, PDO::PARAM_INT);
            $stmt2->execute();
            // 변수 바인드
            echo '1'; // 삭제 성공
        } else {
            echo '2'; // 삭제 실패
        }
    } else {
        echo '3'; // 이미지가 없음
    }

    $stmt->closeCursor();
    $conn = null;



?>