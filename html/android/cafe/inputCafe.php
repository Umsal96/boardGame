<?php

    // 오류가 발생시 출시됨
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베스 연결
    include '../Utility/db_connect.php';

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    $cafeName = $_POST['cafeName'];
    $cafeLat = $_POST['cafeLat'];
    $cafeLnt = $_POST['cafeLnt'];
    $cafeAddress = $_POST['cafeAddress'];
    $cafeContent = $_POST['cafeContent'];

    // 쿼리 작성
    $stmt = $conn->prepare("INSERT INTO cafe_table(cafe_name, cafe_content, cafe_create_date, 
        cafe_lat, cafe_lnt, cafe_address)
        VALUES(:cafe_name, :cafe_content, NOW(), :cafe_lat, :cafe_lnt, :cafe_address)");

    $stmt->bindParam(':cafe_name', $cafeName, PDO::PARAM_STR);
    $stmt->bindParam(':cafe_content', $cafeContent, PDO::PARAM_STR);
    $stmt->bindParam(':cafe_lat', $cafeLat, PDO::PARAM_STR);
    $stmt->bindParam(':cafe_lnt', $cafeLnt, PDO::PARAM_STR);
    $stmt->bindParam(':cafe_address', $cafeAddress, PDO::PARAM_STR);

    $stmt->execute();

    $lastCafeSeq = $conn->lastInsertId();

    // 이미지가 존재하는지 확인
    if(isset($_FILES)) {
        echo '이미지가 존재합니다.';

        $numImages = count($_FILES);
        echo '전송된 이미지의 개수: ' . $numImages;

        // 서버의 파일 경로 지정
        $uploadDir = '/var/www/html/uploads/';

        // 이미지의 순서 설정
        $imageOrder = 1;

        // 순회하면서 각 이미지를 처리
        foreach ($_FILES as $key => $file) {
            if (is_uploaded_file($file['tmp_name'])) {
                echo $file['tmp_name'];
                echo '이미지가 진짜 존재합니다.';
                $fileName = uniqid() . '_' . basename($file['name']);
                $uploadFile = $uploadDir . $fileName;

                // db에 저장될 경로
                $uploadDBDir = '/uploads/' . $fileName;

                // 이미지 저장
                move_uploaded_file($file['tmp_name'], $uploadFile);

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

                // Increment the image order for the next image
                $imageOrder++;

                $lastImgSeq = $conn->lastInsertId();

                $stmt2 = $conn->prepare("INSERT INTO image_to_table(image_seq, cafe_seq, to_create_date)
                VALUES (:image_seq, :cafe_seq, NOW())");

                // 메게변수 바인딩
                $stmt2->bindParam(':image_seq', $lastImgSeq, PDO::PARAM_INT);
                $stmt2->bindParam(':cafe_seq', $lastCafeSeq, PDO::PARAM_INT);

                $stmt2->execute();
            }
        }
    }else {
        echo '이미지가 존재하지 않습니다. ';
    }

    echo '저장 완료';
 
?>