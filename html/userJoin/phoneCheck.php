<?php 

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    $phone = $_GET['phone'];

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    // 핸드폰 번호로 겹치는 데이터의 갯수를 찾음
    $stmt = $conn->prepare("SELECT COUNT(*) FROM user_info WHERE user_phone = :phone");
    $stmt->bindParam(':phone', $phone);
    $stmt->execute();
    $count = $stmt->fetchColumn();
    $stmt->closeCursor();

    if($count > 0){
        echo '2';
    } else {
        echo '1';
    }

    $conn = null;

?>