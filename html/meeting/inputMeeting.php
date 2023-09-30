<?php 

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    // post 로 전송된 데이터를 변수에 담음
    $Suser_id = $_POST['user_id']; // 유저의 고유 아이디
    $user_id = (int) $Suser_id; // 문자열로 받아온 정보를 정수형으로 변환
    $fx = $_POST['x']; // lnt 좌표
    $fy = $_POST['y']; // lat 좌표
    $meetingName = $_POST['name']; // 모임의 이름
    $meetingContent = $_POST['content']; // 모임의 내용
    $meetingSNum = $_POST['member']; // 모임의 최대 인원수
    $meetingNum = (int) $meetingSNum; // 문자열을 정수형으로 변환
    $address = $_POST['address']; // 모임 하려는 주소
    $placeName = $_POST['placeName']; // 모임 장소 이름

    if(!isset($_FILES['image'])){
        $uploadDBDir = null;
    } else {
        // 서버의 파일 경로 지정
        $fileName = uniqid() . '_' . basename($_FILES['image']['name']);
        $uploadDir = '/var/www/html/uploads/'; // 이미지를 저장할 디렉터리 경로
        $uploadFile = $uploadDir . $fileName;

        // db에 저장될 경로 
        $uploadDBDir = '/uploads/' . $fileName;

        // 이미지 저장
        move_uploaded_file($_FILES['image']['tmp_name'], $uploadFile);
    }

    $stmt = $conn->prepare("INSERT INTO meeting_table (user_seq, meeting_name, meeting_place_name,
        meeting_content, meeting_lat, meeting_lnt, meeting_address, meeting_members, 
        meeting_create_date, meeting_url)
        VALUES (:user_seq, :meeting_name, :meeting_place_name, :meeting_content,
        :meeting_lat, :meeting_lnt, :meeting_address, :meeting_members, NOW(), :meeting_url)");

    // 메개변수 바인딩
    $stmt->bindParam(':user_seq', $user_id, PDO::PARAM_INT);
    $stmt->bindParam(':meeting_name', $meetingName, PDO::PARAM_STR);
    $stmt->bindParam(':meeting_place_name', $placeName, PDO::PARAM_STR);
    $stmt->bindParam(':meeting_content', $meetingContent, PDO::PARAM_STR);
    $stmt->bindParam(':meeting_lat', $fy, PDO::PARAM_STR);
    $stmt->bindParam(':meeting_lnt', $fx, PDO::PARAM_STR);
    $stmt->bindParam(':meeting_address', $address, PDO::PARAM_STR);
    $stmt->bindParam(':meeting_members', $meetingNum, PDO::PARAM_INT);
    $stmt->bindParam(':meeting_url', $uploadDBDir, PDO::PARAM_STR);
    
    // 쿼리 실행
    if($stmt->execute()){
        echo $uploadDBDir;
        echo '입력 성공';
    } else {
        echo '정보 저장하는 중 오류가 발생했습니다.';
    }

?>