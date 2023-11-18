<?php 

    // 오류가 발생시 출력됨
    error_reporting(E_ALL); // 모든 php 오류를 표시하도록 설정합니다.
    ini_set('display_errors', '1'); // 오류를 화면에 표시하도록 설정합니다.

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    $replyRef = $_GET['replyRef'];
    $userId = $_GET['userId'];
    $boardId = $_GET['boardId'];
    $content = $_GET['content'];

    $stmt = $conn->prepare("SELECT MAX(reply_order) AS max_reply_order 
        FROM board_reply WHERE reply_ref = :reply_ref");
    
    $stmt->bindParam(':reply_ref', $replyRef, PDO::PARAM_INT);

    $stmt->execute();

    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    $max_reply_order = $result['max_reply_order'];

    // $max_reply_order를 1 증가시킴
    $new_reply_order = $max_reply_order + 1;

    // 쿼리 작성
    $stmt1 = $conn->prepare("INSERT INTO board_reply (board_seq, user_seq, 
        reply_content, reply_order, reply_ref, reply_create_date)
        VALUES (:board_seq, :user_seq, :reply_content, :reply_order, :reply_ref, NOW())");
    
    $stmt1->bindParam(':board_seq', $boardId, PDO::PARAM_INT);
    $stmt1->bindParam(':user_seq', $userId, PDO::PARAM_INT);
    $stmt1->bindParam(':reply_content', $content, PDO::PARAM_STR);
    $stmt1->bindParam(':reply_order', $new_reply_order, PDO::PARAM_INT);
    $stmt1->bindParam(':reply_ref', $replyRef, PDO::PARAM_INT);

    // 쿼리 실행
    $stmt1->execute();

    echo '입력 성공';

?>