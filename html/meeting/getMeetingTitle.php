<?php 

    // 오류가 발생시 출력됨
    error_reporting(E_ALL); // 모든 php 오류를 표시하도록 설정합니다.
    ini_set('display_errors', '1'); // 오류를 화면에 표시하도록 설정합니다.

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $id = $_GET['id']; // 받아온 모임 고유 아이디를 변수에 넣음

    // 쿼리 작성
    $stmt = $conn->prepare("SELECT meeting_name FROM meeting_table WHERE meeting_seq = :meeting_seq");
    // 변수 바인딩
    $stmt->bindParam(':meeting_seq', $id, PDO::PARAM_INT);

    // 쿼리 실행
    $stmt->execute();

    // 결과 가져오기
    $result = $stmt->fetch(PDO::FETCH_ASSOC);

    // 모임 이름
    $meeting_name = $result['meeting_name'];

    echo $meeting_name;

?>