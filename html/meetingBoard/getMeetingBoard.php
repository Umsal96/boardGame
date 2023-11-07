<?php 

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $board_seq = $_GET['boardId'];

    $stmt = $conn->prepare("SELECT meeting_board.*, user_info.user_url, user_info.user_nickname,
        GROUP_CONCAT(image_table.image_url) AS image_urls
        FROM meeting_board
        JOIN user_info ON meeting_board.user_seq = user_info.user_seq
        LEFT JOIN image_table ON meeting_board.board_seq = image_table.board_seq
        WHERE meeting_board.board_seq = :board_seq
        ORDER BY image_table.image_order");

    // 쿼리에 바인딩
    $stmt->bindParam(':board_seq', $board_seq, PDO::PARAM_INT);

    // 쿼리 실행
    $stmt->execute();

    $result = $stmt->fetch(PDO::FETCH_ASSOC);

    $board_seq = $result['board_seq'];
    $user_seq = $result['user_seq'];
    $meeting_seq = $result['meeting_seq'];
    $board_title = $result['board_title'];
    $board_content = $result['board_content'];
    $board_type = $result['board_type'];
    $board_create_date = $result['board_create_date'];
    $user_url = $result['user_url'];
    $user_nickname = $result['user_nickname'];
    $image_urls  = $result['image_urls'];

    $data = [
        'board_seq' => $board_seq, 
        'user_seq' => $user_seq, 
        'meeting_seq' => $meeting_seq, 
        'board_title' => $board_title, 
        'board_content' => $board_content, 
        'board_type' => $board_type, 
        'board_create_date' => $board_create_date,
        'user_url' => $user_url, 
        'user_nickname' => $user_nickname, 
        'image_urls' => $image_urls
    ];

    echo json_encode($data);

?>