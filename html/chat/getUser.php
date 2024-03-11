<?php 

    // 오류가 발생시 출력됨
    error_reporting(E_ALL); // 모든 php 오류를 표시하도록 설정합니다.
    ini_set('display_errors', '1'); // 오류를 화며ㅐㄴ에 표시하도록 설정합니다.

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    //유저 고유 아이디 받아옴
    $userId = $_GET['userId'];

    // 유저 고유 아이디와 닉네임 유저 프로필 정보를 가져옴 
    $stmt = $conn->prepare("SELECT user_seq, user_nickname, user_url
        FROM user_info
        WHERE user_seq = :user_seq");

    // 쿼리 바인딩
    $stmt->bindParam(':user_seq', $userId, PDO::PARAM_INT);

    // 쿼리 실행
    $stmt->execute();

    $result = $stmt->fetch(PDO::FETCH_ASSOC);

    $user_seq = $result['user_seq'];
    $user_nickname = $result['user_nickname'];
    $user_url = $result['user_url'];

    $data = [
        'user_seq' => $user_seq,
        'user_nickname' => $user_nickname,
        'user_url' => $user_url
    ];

    echo json_encode($data);

?>