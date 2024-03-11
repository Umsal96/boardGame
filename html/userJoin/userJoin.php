<?php 
    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    require '../vendor/autoload.php';

    use Firebase\JWT\JWT;

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    // post 데이터를 가져오기
    $jsonData = $_POST['json_data'];
    $email = $_POST['email'];
    $pass = $_POST['pass'];
    $phone = $_POST['phone'];
    $nick = $_POST['nick'];

    // 비밀번호 해싱 비밀번호 암호화
    $hashed_password = password_hash($pass, PASSWORD_DEFAULT);

    // 쿼리 준비
    $stmt = $conn->prepare("INSERT INTO user_info (user_email, user_pw, user_phone, user_create_date, user_nickname)
        VALUES (:user_email, :user_pw, :user_phone, NOW(), :user_nickname)");

    // 메개변수 바인딩
    $stmt->bindParam(':user_email', $email);
    $stmt->bindParam(':user_pw', $hashed_password);
    $stmt->bindParam(':user_phone', $phone);
    $stmt->bindParam(':user_nickname', $nick);

    if($stmt->execute()){

        $newUserId = $conn->lastInsertId();

        $secretKey = "g3Zd9Rn$!C7HtP5m@Xw8NqA6fDvSbE1j"; // 보안을 위한 시크릿 키

        $userId = $result["user_email"];
        $issuedAt = time();
        $expirationTime = $issuedAt + 60 * 60 * 24; // 토큰 만료 시간 (24시간)

        $payload = array(
            "user_id" => $newUserId,
            "iat" => $issuedAt,
            "exp" => $expirationTime // 시간 설정
        );

        $token = JWT::encode($payload, $secretKey, 'HS256');
        // 유저 정보와 JWT 토큰을 JSON 응답에 포함시켜 반환

        $response = array(
            "status" => "1",
            "user_id" => $newUserId,
            "nickname" => $nick,
            "email" => $email,
            "token" => $token
        );
        echo json_encode($response); // 회ㅓ가입이 성공햇을경우
    } else {
        $response = array(
            "status" => "2",
            "message" => "회원가입이 실패했습니다."
        );
        echo json_encode($response); // 회원가입이 실패했을경우
    }

    $stmt->closeCursor();
    $conn = null;

    
?>