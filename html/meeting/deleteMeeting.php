<?php 

    // 오류가 발생시 출력됨
    error_reporting(E_ALL); // 모든 php 오류를 표시하도록 설정합니다.
    ini_set('display_errors', '1'); // 오류를 화며ㅐㄴ에 표시하도록 설정합니다.

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $id = $_GET['id']; // 받아온 미팅 고유 아이디를 변수에 넣음

    // 쿼리 작성
    $stmt = $conn->prepare("DELETE FROM meeting_table WHERE meeting_seq = :meeting_seq");
    // 변수 바인딩, 미팅 고유 아이디를 조건문 삼아서 해당 쿼리를 삭제함
    $stmt->bindParam(':meeting_seq', $id, PDO::PARAM_INT);

    // 삭제에 성공했다면
    if($stmt->execute()){
        echo '1';
    }else{
        echo '2'; // 실패한다면 2를 출력함
    }

?>