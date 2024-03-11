<?php 

    // 가입한 유저의 정보를 가져옴
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    $replyId = $_GET['replyId'];
    $content = $_GET['content'];

    $stmt = $conn->prepare("UPDATE board_reply
        SET reply_content = :content, reply_modified_date = NOW()
        WHERE reply_seq = :reply_seq");

    $stmt->bindParam(':content', $content, PDO::PARAM_STR);
    $stmt->bindParam(':reply_seq', $replyId, PDO::PARAM_INT);

    $stmt->execute();

    echo '수정 완료';

?>