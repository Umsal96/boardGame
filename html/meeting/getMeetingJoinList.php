<?php 

    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    $user_seq = $_GET['user_seq']; // 유저의 고유 아이디

    $page = $_GET['page']; // 현재 보여야할 페이지

    $limit = $_GET['limit']; // 최대 보여야 하는 갯수

    $sql = "SELECT COUNT(*) AS total FROM meeting_table
            JOIN member_table ON meeting_table.meeting_seq = member_table.meeting_seq
            WHERE member_table.user_seq = :user_seq";

    $stmt1 = $conn->prepare($sql);

    $stmt1->bindParam(':user_seq', $user_seq, PDO::PARAM_INT);

    $stmt1->execute();
    $row = $stmt1->fetch(PDO::FETCH_ASSOC);
    $total_count = $row['total'];  // 컬럼의 갯수를 가져옴

    if ($page <= 0) {
        $page = 1;
    }
    if ($limit <= 0) {
        $limit = 10;
    }

    // 전체 데이터 갯수
    $num = $total_count;

    // 페이지 수를 검사하고 조정
    $totalPages = ceil($num / $limit);

    $start = ($page - 1) * $limit;

    $stmt = $conn->prepare("SELECT meeting_table.*
                FROM meeting_table
                JOIN member_table
                ON meeting_table.meeting_seq = member_table.meeting_seq
                WHERE member_table.user_seq = :user_seq
                ORDER BY meeting_table.meeting_seq DESC
                LIMIT :start, :limit_num");

    $stmt->bindParam(':user_seq', $user_seq, PDO::PARAM_INT);
    $stmt->bindParam(':start', $start, PDO::PARAM_INT);
    $stmt->bindParam(':limit_num', $limit, PDO::PARAM_INT);

    $stmt->execute();

    $result['data'] = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $result['num'] = $num;

    // 겱롸를 json 형식으로 변환
    $jsonResult = json_encode($result);

    echo $jsonResult;

?>