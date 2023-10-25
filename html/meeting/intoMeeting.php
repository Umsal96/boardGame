<?php 

    // 가입한 유저의 정보를 가져옴
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();
    
    // 유저의 고유 아이디
    $user_seq = $_GET['userId'];

    // 미팅의 고유 아이디
    $meeting_seq = $_GET['meetingId'];

    // 쿼리문 작성
    $stmt = $conn->prepare("INSERT INTO member_table(user_seq, meeting_seq, member_create_date)
        VALUES (:user_seq, :meeting_seq, NOW())");

    // 메게변수 바인딩
    $stmt->bindParam(':user_seq', $user_seq, PDO::PARAM_INT);
    $stmt->bindParam(':meeting_seq', $meeting_seq, PDO::PARAM_INT);

    // 쿼리 실행
    $stmt->execute();

    $stmt1 = $conn->prepare("UPDATE meeting_table 
        SET meeting_current = meeting_current + 1
        WHERE meeting_seq = :meeting_seq");

    $stmt1->bindParam(':meeting_seq', $meeting_seq, PDO::PARAM_INT);
    $stmt1->execute();

    $stmt2 = $conn->prepare("DELETE FROM member_wait 
        WHERE user_seq = :user_seq AND meeting_seq = :meeting_seq");

    $stmt2->bindParam(':user_seq', $user_seq, PDO::PARAM_INT);
    $stmt2->bindParam(':meeting_seq', $meeting_seq, PDO::PARAM_INT);

    $stmt2->execute();
    
    echo '입력 성공';
?>