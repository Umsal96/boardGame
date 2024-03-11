<?php 

    // 가입한 유저의 정보를 가져옴
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    $meeting_seq = $_GET['meeting_seq'];

    // member_table 의 member_leader 컬럼과 user_info 의 user_nickname, user_url 컬럼을 받아옴
    // member_table과 user_info 의 user_seq의 컬럼을 기준으로 join 함
    // 이 모임에 가입한 사람들의 닉네임과 프로필 사진의 정보를 클라이언트에 제공
    $sql = "SELECT member_table.member_leader, member_table.user_seq, user_info.user_nickname, user_info.user_url
        FROM member_table 
        JOIN user_info ON member_table.user_seq = user_info.user_seq
        WHERE member_table.meeting_seq = :meeting_seq
        ORDER BY member_table.member_leader DESC";
    
    $stmt = $conn->prepare($sql);
    
    // 쿼리에 바인딩
    $stmt->bindParam(':meeting_seq', $meeting_seq, PDO::PARAM_INT);

    // 쿼리 실행
    $stmt->execute();

    // 결과를 받아옴
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // 결과를 json 형식으로 변환
    $jsonResult = json_encode($result);

    echo $jsonResult;

?>