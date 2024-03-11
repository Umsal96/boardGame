<?php 

    // 오류가 발생시 출력됨
    error_reporting(E_ALL); // 모든 php 오류를 표시하도록 설정합니다.
    ini_set('display_errors', '1'); // 오류를 화며ㅐㄴ에 표시하도록 설정합니다.

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $id = $_GET['id']; // 받아온 모임 고유 아이디를 변수에 넣음

    // 쿼리 작성
    $stmt = $conn->prepare("SELECT * FROM meeting_table WHERE meeting_seq = :meeting_seq");
    // 변수 바인딩
    $stmt->bindParam(':meeting_seq', $id, PDO::PARAM_INT);

    $stmt->execute();

    $result = $stmt->fetch(PDO::FETCH_ASSOC);

    // 결과를 연관 배열에서 직접 추출
    // 모임 고유 아이디
    $meeting_seq = $result['meeting_seq'];
    // 모임 생성자 고유 아이디
    $user_seq = $result['user_seq'];
    // 모임 이름
    $meeting_name = $result['meeting_name'];
    // 모임 장소 이름
    $meeting_place_name = $result['meeting_place_name'];
    // 모임 내용
    $meeting_content = $result['meeting_content'];
    // y좌표
    $meeting_lat = $result['meeting_lat'];
    // x 좌표
    $meeting_lnt = $result['meeting_lnt'];
    // 모임 주소
    $meeting_address = $result['meeting_address'];
    // 모임 최대 인원수
    $meeting_members = $result['meeting_members'];
    // 모임 생성 날짜
    $meeting_create_date = $result['meeting_create_date'];
    // 모임 수정 날짜
    $meeting_modified_date = $result['meeting_modified_date'];
    // 모임 대표 이미지 url
    $meeting_url = $result['meeting_url'];
    // 모임 현재 인원수
    $meeting_current = $result['meeting_current'];


    $data = [
        'meeting_seq' => $meeting_seq,
        'user_seq' => $user_seq,
        'meeting_name' => $meeting_name,
        'meeting_place_name' => $meeting_place_name,
        'meeting_content' => $meeting_content,
        'meeting_lat' => $meeting_lat,
        'meeting_lnt' => $meeting_lnt,
        'meeting_address' => $meeting_address,
        'meeting_members' => $meeting_members,
        'meeting_create_date' => $meeting_create_date,
        'meeting_modified_date' => $meeting_modified_date,
        'meeting_url' => $meeting_url,
        'meeting_current' => $meeting_current
    ];

    echo json_encode($data);
    
?>