<?php 

    // 가입한 유저의 정보를 가져옴
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    $replyId = $_GET['replyId'];
    $replyRef = $_GET['replyRef'];

    $replyDel = 1;

    // reply_del 값은 1로 변경시키는 쿼리
    $stmt = $conn->prepare("UPDATE board_reply
        SET reply_del = :reply_del
        WHERE reply_seq = :reply_seq");

    $stmt->bindParam(':reply_del', $replyDel, PDO::PARAM_INT);
    $stmt->bindParam(':reply_seq', $replyId, PDO::PARAM_INT);

    $stmt->execute();

    $stmt1 = $conn->prepare("SELECT COUNT(*) AS total FROM board_reply 
        WHERE reply_ref = :reply_ref AND reply_del = 0");

    $stmt1->bindParam(':reply_ref', $replyRef, PDO::PARAM_INT);

    $stmt1->execute();

    $row = $stmt1->fetch(PDO::FETCH_ASSOC);
    $total_count = $row['total'];

    if($total_count > 0){
        echo 'del 값이 0 인게 있음 ';
    } else {
        $stmt2 = $conn->prepare("DELETE FROM board_reply 
        WHERE reply_ref = :reply_ref");

        $stmt2->bindParam(':reply_ref', $replyRef, PDO::PARAM_INT);

        $stmt2->execute();

        echo '성공';
    }

    // echo "삭제 성공";
?>