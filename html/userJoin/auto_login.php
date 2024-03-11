<?php

require '../vendor/autoload.php';

use Firebase\JWT\JWT;

// 시크릿 키 설정 (반드시 안전하게 관리해야 함)
$secretKey = 'g3Zd9Rn$!C7HtP5m@Xw8NqA6fDvSbE1j';

// 클라이언트로부터 전달받은 토큰
$receivedToken = $_POST["token"];

try {
    // 토큰 검증
    $decodedToken = JWT::decode($receivedToken, $secretKey, array('HS256'));
    $userInfoFromToken = $decodedToken->userInfo;

    // 토큰이 유효한 경우, 사용자 정보를 JSON 형태로 반환
    $response = array(
        "status" => "1",
        "nickname" => $userInfoFromToken->nickname,
        // 추가적인 사용자 정보들도 반환할 수 있음
    );
    echo json_encode($response);
} catch (Exception $e) {
    // 토큰이 유효하지 않을 때 처리
    $response = array(
        "status" => "0",
        "message" => "자동 로그인 실패"
    );
    echo json_encode($response);
}

?>