<?php 

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $meetingId = $_GET['meetingId'];

    $stmt = $conn->prepare("SELECT message_table.*, 
    user_info.user_nickname, user_info.user_url 
    FROM message_table
    JOIN user_info ON message_table.user_seq = user_info.user_seq
    WHERE message_table.meeting_seq = :meeting_seq");

    // 쿼리에 바인딩
    $stmt->bindParam(':meeting_seq', $meetingId, PDO::PARAM_INT);

    // 쿼리 실행
    $stmt->execute();

    // 결과 받아옴
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // 결과를 json 형식으로 변환
    $jsonResult = json_encode($result);

    echo $jsonResult;

?>