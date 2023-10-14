<?php 

    // 오류가 발생시 출력됨
    error_reporting(E_ALL); // 모든 php 오류를 표시하도록 설정합니다.
    ini_set('display_errors', '1'); // 오류를 화며ㅐㄴ에 표시하도록 설정합니다.

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $schedule = $_GET['schedule']; // 일정 고유 아이디

    $stmt = $conn->prepare("DELETE FROM meeting_schedule_table WHERE
        schedule_seq = :schedule_seq");
    
    $stmt->bindParam(':schedule_seq', $schedule, PDO::PARAM_INT);
    $stmt->execute();
    
    echo "삭제 성공";

?>