<?php 

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 이메일을 get으로 받음
    $email = $_GET['email'];

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $stmt = $conn->prepare("SELECT COUNT(*) FROM user_info WHERE user_email = :email");
    $stmt->bindParam(':email', $email);
    $stmt->execute();
    $count = $stmt->fetchColumn();
    $stmt->closeCursor();

    if($count > 0){ // 중복이 되었다면
        echo '2';
    } else { // 중복이 되어있지 않다면
        echo '1';
    }

    $conn = null;
?>