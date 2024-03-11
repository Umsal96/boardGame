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

    $search = $_GET['search']; // 검색어

    $type = $_GET['type']; // 어디서 검색했는지

    $checkQuery = "SELECT COUNT(*) as count FROM search_table
    WHERE user_seq = :user_seq AND search_content = :search_content
    AND search_type = :search_type";

    $stmtCheck = $conn->prepare($checkQuery);
    $stmtCheck->bindParam(':user_seq', $user_seq, PDO::PARAM_INT);
    $stmtCheck->bindParam(':search_content', $search, PDO::PARAM_STR);
    $stmtCheck->bindParam(':search_type', $type, PDO::PARAM_STR);
    $stmtCheck->execute();
    $result = $stmtCheck->fetch(PDO::FETCH_ASSOC);

    if($result['count'] > 0){
        // 중복된 경우, 업데이트
        $updateQuery = "UPDATE search_table SET search_create_date = NOW()
                    WHERE user_seq = :user_seq AND search_content = :search_content
                    AND search_type = :search_type";

        $stmtUpdate = $conn->prepare($updateQuery);
        $stmtUpdate->bindParam(':user_seq', $user_seq, PDO::PARAM_INT);
        $stmtUpdate->bindParam(':search_content', $search, PDO::PARAM_STR);
        $stmtUpdate->bindParam(':search_type', $type, PDO::PARAM_STR);
        $stmtUpdate->execute();
    } else {
        // 중복되지 않은 경우, 삽입
        $insertQuery = "INSERT INTO search_table (user_seq, search_content, search_create_date, search_type)
        VALUES (:user_seq, :search_content, NOW(), :search_type)";

        $stmtInsert = $conn->prepare($insertQuery);
        $stmtInsert->bindParam(':user_seq', $user_seq, PDO::PARAM_INT);
        $stmtInsert->bindParam(':search_content', $search, PDO::PARAM_STR);
        $stmtInsert->bindParam(':search_type', $type, PDO::PARAM_STR);
        $stmtInsert->execute();
    }

    $stmt2 = $conn->prepare("SELECT COUNT(*) AS total FROM game_table 
    WHERE game_name LIKE :search");

    $searchWord = "%$search%";

    $stmt2->bindParam(':search', $searchWord, PDO::PARAM_STR);

    $stmt2->execute();
    $row = $stmt2->fetch(PDO::FETCH_ASSOC);
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
        AND game_table.game_name LIKE :search
    GROUP BY 
        game_table.game_seq, image_seq, image_url
    ORDER BY 
        game_table.game_seq DESC
    LIMIT :start, :limit_num";

    $stmt3 = $conn->prepare($sql);
    $stmt3->bindParam(':start', $start, PDO::PARAM_INT);
    $stmt3->bindParam(':limit_num', $limit, PDO::PARAM_INT);
    $stmt3->bindParam(':search', $searchWord, PDO::PARAM_STR);

    // 쿼리 실행
    $stmt3->execute();

    $result['data'] = $stmt3->fetchAll(PDO::FETCH_ASSOC);

    $result['num'] = $num;

    // 겱롸를 json 형식으로 변환
    $jsonResult = json_encode($result);

    echo $jsonResult;
?>