<?php

    // 가입한 유저의 정보를 가져옴
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();
    
    $cafeId = $_POST['cafeId'];
    $foodName = $_POST['foodName'];
    $foodPrice = $_POST['foodPrice'];

    $stmt = $conn->prepare("INSERT INTO cafe_food_table(cafe_seq, food_name,
        food_price, food_create_date)
        VALUES (:cafe_seq, :food_name, :food_price, NOW())");

    $stmt->bindParam(':cafe_seq', $cafeId, PDO::PARAM_INT);
    $stmt->bindParam(':food_name', $foodName, PDO::PARAM_STR);
    $stmt->bindParam(':food_price', $foodPrice, PDO::PARAM_INT);

    $stmt->execute();

    $lastFoodSeq = $conn->lastInsertId();

    if (!isset($_FILES['image'])) {
        echo '이미지가 전송되지 않았습니다.';
        exit; // 스크립트 실행 중지
    }

    // 이미지의 순서 설정
    $imageOrder = 1;

    // 서버의 파일 경로 지정
    $fileName = uniqid() . '_' . basename($_FILES['image']['name']);
    $uploadDir = '/var/www/html/uploads/'; // 이미지를 저장할 디렉터리 경로
    $uploadFile = $uploadDir . $fileName;

    // db에 저장될 경로 
    $uploadDBDir = '/uploads/' . $fileName;

    // 이미지 저장
    move_uploaded_file($_FILES['image']['tmp_name'], $uploadFile);

    // 이미지를 데이터 베이스에 저장
    $stmt1 = $conn->prepare("INSERT INTO image_table (image_url, 
    image_create, image_order) VALUES (:image_url, NOW(), :image_order)");

    // 메게변수 바인딩
    $stmt1->bindParam(':image_url', $uploadDBDir, PDO::PARAM_STR);
    $stmt1->bindParam(':image_order', $imageOrder, PDO::PARAM_INT);

    $result1 = $stmt1->execute();

    if (!$result1) {
        print_r($stmt1->errorInfo());
    }

    $lastImgSeq = $conn->lastInsertId();

    $stmt2 = $conn->prepare("INSERT INTO image_to_table(image_seq, food_seq, to_create_date)
        VALUES (:image_seq, :food_seq, NOW())");

    // 메게변수 바인딩
    $stmt2->bindParam(':image_seq', $lastImgSeq, PDO::PARAM_INT);
    $stmt2->bindParam(':food_seq', $lastFoodSeq, PDO::PARAM_INT);

    $stmt2->execute();

    echo '저장 완료';
?>