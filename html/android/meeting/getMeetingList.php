<?php 

    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    $page = $_GET['page']; // 현재 보여야할 페이지

    $limit = $_GET['limit']; // 최대 보여야 하는 갯수

    $sql = "SELECT COUNT(*) AS total FROM meeting_table";

    $stmt1 = $conn->prepare($sql);

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
    // if($page > $totalPages){
    //     $page = $totalPages; // 페이지가 너무 큰 경우 마지막 페이지로 조정
    // }
    $start = ($page - 1) * $limit;

    $stmt = $conn->prepare("SELECT meeting_seq, meeting_name, meeting_content, 
        meeting_members, meeting_create_date, meeting_url, meeting_current 
        FROM meeting_table ORDER BY meeting_seq DESC 
        LIMIT :start, :limit_num");

        $stmt->bindParam(':start', $start, PDO::PARAM_INT);
        $stmt->bindParam(':limit_num', $limit, PDO::PARAM_INT);
    
    $stmt->execute();

    // 원래의 결과 배열을 "data" 키에 할당
    $result['data'] = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // $num도 결과 배열에 추가
    $result['num'] = $num;

    // 결과를 JSON 형식으로 변환
    $jsonResult = json_encode($result);

    // JSON 출력
    echo $jsonResult;


?>