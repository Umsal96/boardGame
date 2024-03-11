<?php 

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    // 로그인 정보 가져옴
    $user_email = $_POST["email"];
    $user_pw = $_POST["pass"];

    // update 쿼리문 작성
    $stmt = $conn->prepare("UPDATE user_info SET user_pw = :user_pw, user_modified_date = NOW() WHERE user_email = :user_email");

    // 받아온 비밀번호를 해신 비밀번호 암호화
    $hashed_password = password_hash($user_pw, PASSWORD_DEFAULT);

    // 변수 바인드
    $stmt->bindParam(':user_pw', $hashed_password, PDO::PARAM_STR);
    $stmt->bindParam(':user_email', $user_email, PDO::PARAM_STR);

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