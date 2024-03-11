<?php 

    // 오류가 발생시 출력됨
    error_reporting(E_ALL); // 모든 php 오류를 표시하도록 설정합니다.
    ini_set('display_errors', '1'); // 오류를 화면에 표시하도록 설정합니다.

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $userId = $_GET['userId']; // 유저의 고유 아이디
    $meetingId = $_GET['id']; // 모임의 고유 아이디

    $stmt = $conn->prepare("SELECT * FROM member_wait WHERE 
            user_seq = :user_seq AND meeting_seq = :meeting_seq");

    $stmt->bindParam(':user_seq', $userId, PDO::PARAM_INT);
    $stmt->bindParam(':meeting_seq', $meetingId, PDO::PARAM_INT);

    $stmt->execute();

    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $jsonResult = json_encode($result);

    echo $jsonResult
?>