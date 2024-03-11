<?php 

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $page = $_GET['page'];
    $limit = $_GET['limit'];

    $stmt1 = $conn->prepare("SELECT COUNT(*) AS total FROM game_table");

    $stmt1->execute();
    $row = $stmt1->fetch(PDO::FETCH_ASSOC);
    $total_count = $row['total']; // 컬럼의 갯수를 가져옴

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

    $sql = "SELECT game_table.*,
        (SELECT image_seq FROM image_to_table WHERE image_to_table.game_seq = game_table.game_seq LIMIT 1) AS image_seq,
        (SELECT image_url FROM image_table WHERE image_table.image_seq = (
            SELECT image_seq 
            FROM image_to_table 
            WHERE image_to_table.game_seq = game_table.game_seq 
            LIMIT 1
        )
        ) AS image_url,
        AVG(COALESCE(review_table.review_grade, NULL)) AS average_review_grade
    FROM game_table
    LEFT JOIN 
    review_table ON game_table.game_seq = review_table.game_seq
    WHERE 
        game_table.game_seq = game_table.game_seq  -- Replace :your_game_seq with the specific game_seq you're interested in
    GROUP BY 
        game_table.game_seq, image_seq, image_url
    ORDER BY 
        game_table.game_seq DESC
    LIMIT :start, :limit_num";

    $stmt = $conn->prepare($sql);
    $stmt->bindParam(':start', $start, PDO::PARAM_INT);
    $stmt->bindParam(':limit_num', $limit, PDO::PARAM_INT);

    // 쿼리 실행
    $stmt->execute();

    $result['data'] = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $result['num'] = $num;

    // 겱롸를 json 형식으로 변환
    $jsonResult = json_encode($result);

    echo $jsonResult;

?>