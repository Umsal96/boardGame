<?php
if ($_SERVER['REQUEST_METHOD'] === 'POST') {

    include './db_connect.php';

    $conn = connectToDatabase();

    // 전송한 유저의 고유 아이디를 받음
    $Suser_id = $_POST['user_id'];

    $user_id = (int) $Suser_id;

    if (!isset($_FILES['image'])) {
        echo '이미지가 전송되지 않았습니다.';
        exit; // 스크립트 실행 중지
    }

    // 서버의 파일 경로 지정
    $fileName = uniqid() . '_' . basename($_FILES['image']['name']);
    $uploadDir = '/var/www/html/uploads/'; // 이미지를 저장할 디렉터리 경로
    $uploadFile = $uploadDir . $fileName;

    // db에 저장될 경로 
    $uploadDBDir = '/uploads/' . $fileName;

    // 이전 이미지 경로 가져오기
    $stmt = $conn->prepare("SELECT user_url FROM user_info WHERE user_seq = :user_seq");
    $stmt->bindParam(':user_seq', $user_id, PDO::PARAM_INT);
    $stmt->execute();
    $previousImage = $stmt->fetchColumn();

    $preUploadDir = '/var/www/html';

        // 이전 이미지 삭제
        if ($previousImage && file_exists($preUploadDir . $previousImage)) {
            unlink($preUploadDir . $previousImage);
        }

    if (move_uploaded_file($_FILES['image']['tmp_name'], $uploadFile)) {
        // 쿼리문 작성
        $stmt2 = $conn->prepare("UPDATE user_info SET user_url = :user_url, user_modified_date = NOW() WHERE user_seq = :user_seq");
        // 변수 바인드
        $stmt2->bindParam(':user_url', $uploadDBDir, PDO::PARAM_STR);
        $stmt2->bindParam(':user_seq', $user_id, PDO::PARAM_INT);
        $result = $stmt2->execute();
        
        echo $uploadDBDir;
    } else {
        echo '이미지를 저장하는 중 오류가 발생했습니다.';
    }
} else {
    echo '올바른 요청이 아닙니다.';
}
?>
