<?php 

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $meeting_seq = $_GET['meeting_seq'];
    $user_seq = $_GET['user_seq'];
    $read_time = $_GET['read_time'];
    $chat_seq = $_GET['chat_seq'];

    $stmt = $conn->prepare("UPDATE read_table 
        SET read_time = :read_time, chat_seq = :chat_seq
        WHERE meeting_seq = :meeting_seq AND user_seq = :user_seq");

    $stmt->bindParam(':read_time', $read_time, PDO::PARAM_STR);
    $stmt->bindParam(':chat_seq', $chat_seq, PDO::PARAM_INT);
    $stmt->bindParam(':meeting_seq', $meeting_seq, PDO::PARAM_INT);
    $stmt->bindParam(':user_seq', $user_seq, PDO::PARAM_INT);

    // 쿼리 실해
    if($stmt->execute()){
        echo '수정 성공';
    } else{
        echo '수정 실패';
    }
?>