<?php 

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $meeting_seq = $_POST['meeting_seq'];
    $user_seq = $_POST['user_seq'];
    $message_content = $_POST['message_content'];
    $message_read = $_POST['message_read'];
    $message_date = $_POST['message_date'];

    // 채팅 내용을 db 에 저장하는 쿼리
    $stmt = $conn->prepare("INSERT INTO message_table (meeting_seq,
    user_seq, message_content, message_read, message_date)
    VALUES (:meeting_seq, :user_seq, 
    :message_content, :message_read, :message_date)");

    // 매게변수 바인딩
    $stmt->bindParam(':meeting_seq', $meeting_seq, PDO::PARAM_INT);
    $stmt->bindParam(':user_seq', $user_seq, PDO::PARAM_INT);
    $stmt->bindParam(':message_content', $message_content, PDO::PARAM_STR);
    $stmt->bindParam(':message_read', $message_read, PDO::PARAM_INT);
    $stmt->bindParam(':message_date', $message_date, PDO::PARAM_STR);

    if($stmt->execute()){
        $lastMeetingSeq = $conn->lastInsertId();
        echo $lastMeetingSeq;
    } else{
        echo '저장실패';
    }
    
?>