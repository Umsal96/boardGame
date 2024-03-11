<?php

    // 가입한 유저의 정보를 가져옴
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    $boardId = $_GET['boardId'];

    $stmt = $conn->prepare("SELECT board_reply.*, user_info.user_url, user_info.user_nickname
        FROM board_reply
        JOIN user_info ON board_reply.user_seq = user_info.user_seq
        WHERE board_reply.board_seq = :boardId
        ORDER BY board_reply.reply_ref, board_reply.reply_order");

    $stmt->bindParam(':boardId', $boardId, PDO::PARAM_INT);

    // 쿼리 실행
    $stmt->execute();

    //결과를 받음
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // 결과를 json 형식으로 변환
    $jsonResult = json_encode($result);

    echo $jsonResult;

?>