<?php 

    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    $conn->exec("SET time_zone = '+09:00'"); // KST는 UTC+9

    $meetingSeq = $_GET['id']; // Get 으로 받아온 모임 고유 아이디를 변수에 넣음

    // 현재 날짜와 시간을 구합니다
    $currentDateTime = new DateTime();

    // 30분을 추가한 시간을 계산합니다.
    $currentDateTime->add(new DateInterval('PT30M'));

    // 현재 날짜와 시간을 문자열로 변환합니다.
    $currentDateTimeStr = $currentDateTime->format('Y-m-d H:i:s');

    // 미팅 아이디를 과 현재 시간을 기준으로 일정 멤버를 가져옴
    // 일정은 현재 시간을 기준으로 30분이 지나지 않은것 만을 가져오기 떄문에 맴버도 30분이 지나지 않은 미팅의 고유 아이디와 
    // 시간을 기준으로 컬럼을 조회해서 가져감
    $stmt = $conn->prepare("SELECT schedule_member.*, 
        meeting_schedule_table.schedule_date, meeting_schedule_table.schedule_time
        FROM schedule_member 
        JOIN meeting_schedule_table
        ON schedule_member.schedule_seq = meeting_schedule_table.schedule_seq
        WHERE schedule_member.meeting_seq = :meeting_seq
        AND CONCAT(meeting_schedule_table.schedule_date, ' ', meeting_schedule_table.schedule_time) > DATE_ADD(NOW(), INTERVAL 30 MINUTE)");


    $stmt->bindParam(':meeting_seq', $meetingSeq, PDO::PARAM_INT);
    // $stmt->bindParam(':current_datetime', $currentDateTimeStr, PDO::PARAM_STR);
    $stmt->execute();

    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($result);
?>
