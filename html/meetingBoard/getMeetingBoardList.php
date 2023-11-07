<?php 

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $type = $_GET['type'];

    if($type == '전체'){
        $sql = "SELECT meeting_board.*, user_info.user_url, user_info.user_nickname,
            (SELECT image_url FROM image_table WHERE image_table.board_seq = meeting_board.board_seq LIMIT 1) AS image_url
            FROM meeting_board
            JOIN user_info ON meeting_board.user_seq = user_info.user_seq
            ORDER BY meeting_board.board_seq DESC";
    }else{
        $sql = "SELECT meeting_board.*, user_info.user_url, user_info.user_nickname,
            (SELECT image_url FROM image_table WHERE image_table.board_seq = meeting_board.board_seq LIMIT 1) AS image_url
            FROM meeting_board
            JOIN user_info ON meeting_board.user_seq = user_info.user_seq
            WHERE meeting_board.board_type = :category
            ORDER BY meeting_board.board_seq DESC";
    }

    $stmt = $conn->prepare($sql);

    // 만약 $type에 따라 다른 조건을 추가해야 하는 경우,
    // 바인딩된 매개변수를 설정하여 :type를 해당 컬럼명 또는 값으로 변경
    if ($type != '전체') {
        $stmt->bindParam(':category', $type, PDO::PARAM_STR);
    }

    // 쿼리 실행
    $stmt->execute();

    //결과를 받음
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // 결과를 json 형식으로 변환
    $jsonResult = json_encode($result);

    echo $jsonResult;

?>
