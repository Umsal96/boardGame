<?php 

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $name = $_POST['name'];

    // 서버의 파일 경로 지정
    $fileName = uniqid() . '_' . basename($_FILES['image']['name']);
    $uploadDir = '/var/www/html/uploads/'; // 이미지를 저장할 디렉터리 경로
    $uploadFile = $uploadDir . $fileName;

    // db에 저장될 경로 
    $uploadDBDir = '/uploads/' . $fileName;

    // 이미지 저장
    move_uploaded_file($_FILES['image']['tmp_name'], $uploadFile);

    // 이미지를 데이터 베이스에 저장
    $stmt = $conn->prepare("INSERT INTO ready_game (ready_name, ready_url) 
    VALUES (:ready_name, :ready_url)");

    // 매개변수 바인딩
    $stmt->bindParam(':ready_name', $name, PDO::PARAM_STR);
    $stmt->bindParam(':ready_url', $uploadDBDir, PDO::PARAM_STR);

    $stmt->execute();

    echo '저장 완료';
?>