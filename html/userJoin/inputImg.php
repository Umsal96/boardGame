<?php 

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    //수정 정보 가져옴
    $user_url = $_POST["url"];
    $sid = $_POST["id"];

    $id = $sid;

    // update 쿼리문 작성
    $stmt = $conn->prepare("UPDATE user_info SET user_url = :user_url, user_modified_date = NOW() WHERE user_seq = :user_seq");

    // 변수 바인드
    $stmt->bindParam(':user_url', $user_url, PDO::PARAM_STR);
    $stmt->bindParam(':user_seq', $id, PDO::PARAM_INT);
    
    if($stmt->execute()){
        http_response_code(200);
        exit; 
    } else {
        http_response_code(500);
        exit;
    }
    
    $stmt->close();
    $conn = null;

?>