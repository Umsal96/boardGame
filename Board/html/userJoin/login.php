<?php 
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    require '../vendor/autoload.php';

    use Firebase\JWT\JWT;

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    // 로그인 정보 가져옴
    $user_email = $_POST["email"];
    $user_pw = $_POST["pass"];

    // 쿼리문 작성
    $stmt = $conn->prepare("SELECT * FROM user_info WHERE user_email = :user_email");

    // 바인딩
    $stmt->bindParam(':user_email', $user_email, PDO::PARAM_STR);

    // 실행
    $stmt->execute();

    // 아이디로 검색했을 때 나온 결과물을 result 객체에 넣는다.
    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    
    // 로그인 정보 확인
    if ($result) {
        // 비밀번호 확인
        if (password_verify($user_pw, $result["user_pw"])) {
            // JWT 토큰 생성
            $secretKey = "g3Zd9Rn$!C7HtP5m@Xw8NqA6fDvSbE1j"; // 보안을 위한 시크릿 키

            $userId = $result["user_email"];
            $issuedAt = time();
            $expirationTime = $issuedAt + 60 * 60 * 24; // 토큰 만료 시간 (24시간)

            $payload = array(
                "user_id" => $userId,
                "seq" => $result["user_seq"],
                "iat" => $issuedAt,
                "exp" => $expirationTime // 시간 설정
            );

            $token = JWT::encode($payload, $secretKey, 'HS256');
            // 유저 정보와 JWT 토큰을 JSON 응답에 포함시켜 반환

            $response = array(
                "status" => "1",
                "user_id" => $result["user_seq"],
                "nickname" => $result["user_nickname"],
                "email" => $result["user_email"],
                "url" => $result["user_url"],
                "token" => $token
            );
            echo json_encode($response); // 로그인 성공과 닉네임 반환
        } else{
            $response = array(
                "status" => "2",
                "message" => "비밀번호가 일치하지 않습니다."
            );
            echo json_encode($response); // 비밀번호가 일치하지 않음 반환
        }
    }else {
        $response = array(
            "status" => "3",
            "message" => "해당하는 사용자 정보가 없습니다."
        );
        echo json_encode($response); // 해당하는 유저 정보가 없음 반환
    }

    $stmt->close();
    $conn = null;

?>
