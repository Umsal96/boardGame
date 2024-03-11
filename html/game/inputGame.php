<?php

    // 가입한 유저의 정보를 가져옴
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    // post로 전송횐 데이터를 변수에 담음
    $gameName = $_POST['gameName'];
    $gameSummary = $_POST['gameSummary'];

    $gameMin = $_POST['gameMax'];
    $gameMax = $_POST['gameMin'];
    $gameDetail = $_POST['gameDetail'];

    // 쿼리 작성
    $stmt = $conn->prepare("INSERT INTO game_table (game_name, game_summary, game_min,
        game_max, game_detail, game_create_data)
        VALUES (:game_name, :game_summary, :game_min, :game_max,
        :game_detail, NOW())");

    $stmt->bindParam(':game_name', $gameName, PDO::PARAM_STR);
    $stmt->bindParam(':game_summary', $gameSummary, PDO::PARAM_STR);
    $stmt->bindParam(':game_min', $gameMin, PDO::PARAM_INT);
    $stmt->bindParam(':game_max', $gameMax, PDO::PARAM_INT);
    $stmt->bindParam(':game_detail', $gameDetail, PDO::PARAM_STR);

    $stmt->execute();

    $lastGameSeq = $conn->lastInsertId();

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

                $stmt2 = $conn->prepare("INSERT INTO image_to_table(image_seq, game_seq, to_create_date)
                VALUES (:image_seq, :game_seq, NOW())");

                // 메게변수 바인딩
                $stmt2->bindParam(':image_seq', $lastImgSeq, PDO::PARAM_INT);
                $stmt2->bindParam(':game_seq', $lastGameSeq, PDO::PARAM_INT);

                $stmt2->execute();
            }
        }
    }else {
        echo '이미지가 존재하지 않습니다. ';
    }

    echo '저장 완료';
    
?>