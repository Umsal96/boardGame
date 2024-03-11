<?php 

    // 오류가 발생시 출력됨
    error_reporting(E_ALL); // 모든 php 오류를 표시하도록 설정합니다.
    ini_set('display_errors', '1'); // 오류를 화면에 표시하도록 설정합니다.

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    $replyRef = $_GET['replyRef'];

    $stmt = $conn->prepare("SELECT board_reply.*, user_info.user_url, user_info.user_nickname
    FROM board_reply
    JOIN user_info ON board_reply.user_seq = user_info.user_seq
    WHERE board_reply.reply_ref = :reply_ref
    ORDER BY board_reply.reply_ref, board_reply.reply_order");

    $stmt->bindParam(':reply_ref', $replyRef, PDO::PARAM_INT);

    // 쿼리 실행
    $stmt->execute();

    //결과를 받음
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // 결과를 json 형식으로 변환
    $jsonResult = json_encode($result);

    echo $jsonResult;

?>