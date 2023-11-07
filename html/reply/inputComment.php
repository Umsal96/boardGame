<?php 

    // 가입한 유저의 정보를 가져옴
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    $userId = $_GET['userId'];
    $boardId = $_GET['boardId'];
    $content = $_GET['content'];

    $order = 0;   
    
    // 쿼리 작성
    $stmt = $conn->prepare("INSERT INTO board_reply (board_seq, user_seq, 
        reply_content, reply_order, reply_create_date)
        VALUES (:board_seq, :user_seq, :reply_content, :reply_order
        , DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s'))");

    // 메개변수 바인딩
    $stmt->bindParam(':board_seq', $boardId, PDO::PARAM_INT);
    $stmt->bindParam(':user_seq', $userId, PDO::PARAM_INT);
    $stmt->bindParam(':reply_content', $content, PDO::PARAM_STR);
    $stmt->bindParam(':reply_order', $order, PDO::PARAM_INT);

    // 쿼리 실행
    $stmt->execute();

    // 방금 추가된 댓글의 reply_seq 값을 가져옴
    $reply_seq = $conn->lastInsertId();

    // reply_ref에 reply_seq 값을 업데이트합니다.
    $stmt1 = $conn->prepare("UPDATE board_reply SET reply_ref = :reply_seq WHERE reply_seq = :reply_seq");
    $stmt1->bindParam(':reply_seq', $reply_seq);
    $stmt1->execute();

    echo '입력 성공';


?>