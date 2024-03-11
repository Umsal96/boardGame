<?php 

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 닉네임을 get으로 받음
    $nickName = $_GET['nick'];

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $stmt = $conn->prepare("SELECT COUNT(*) FROM user_info WHERE user_nickname = :nickName");
    $stmt->bindParam(':nickName', $nickName, PDO::PARAM_STR);
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