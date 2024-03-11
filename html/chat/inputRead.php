<?php 

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $user_seq = $_POST['user_seq'];
    $meeting_seq = $_POST['meeting_seq'];
    $read_time = $_POST['read_time'];
    $chat_seq = $_POST['chat_seq'];

    // 채팅의 읽음 처리를 위한 db 에 어디까지 읽었는지 저장 하기위한 쿼리문
    $stmt = $conn->prepare("INSERT INTO read_table (user_seq, 
    meeting_seq, read_time, chat_seq)
    VALUES (:user_seq, :meeting_seq, :read_time, :chat_seq)");

    // 매게변수 바인딩
    $stmt->bindParam(':user_seq', $user_seq, PDO::PARAM_INT);
    $stmt->bindParam(':meeting_seq', $meeting_seq, PDO::PARAM_INT);
    $stmt->bindParam(':read_time', $read_time, PDO::PARAM_STR);
    $stmt->bindParam(':chat_seq', $chat_seq, PDO::PARAM_INT);

    if($stmt->execute()){
        echo '저장왼료';
    } else{
        echo '저장실패';
    }

?>