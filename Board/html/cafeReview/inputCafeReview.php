<?php 

    // 가입한 유저의 정보를 가져옴
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    $userId = $_POST['userId'];
    $cafeId = $_POST['cafeId'];
    $reviewContent = $_POST['reviewContent'];
    $reviewGrade = $_POST['reviewGrade'];
    $reviewType = 1; // 0 이면 게임 리뷰 1 이면 카페 리뷰
 
    $stmt = $conn->prepare("INSERT INTO review_table (user_seq, cafe_seq,
        review_grade, review_content, review_type, review_create_date)
        VALUES (:user_seq, :cafe_seq,
        :review_grade, :review_content, :review_type, NOW())");

    // 메개변수 바인딩
    $stmt->bindParam(':user_seq', $userId, PDO::PARAM_INT);
    $stmt->bindParam(':cafe_seq', $cafeId, PDO::PARAM_INT);
    $stmt->bindParam(':review_grade', $reviewGrade, PDO::PARAM_INT);
    $stmt->bindParam(':review_content', $reviewContent, PDO::PARAM_STR);
    $stmt->bindParam(':review_type', $reviewType, PDO::PARAM_INT);

    // 쿼리 실행
    $stmt->execute();

    $lastReviewSeq = $conn->lastInsertId();

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
                $imagePlace = 1;  // 장소 데이터 저장
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

                $stmt2 = $conn->prepare("INSERT INTO image_to_table(image_seq, review_seq, to_create_date)
                VALUES (:image_seq, :review_seq, NOW())");

                // 메게변수 바인딩
                $stmt2->bindParam(':image_seq', $lastImgSeq, PDO::PARAM_INT);
                $stmt2->bindParam(':review_seq', $lastReviewSeq, PDO::PARAM_INT);

                $stmt2->execute();
            }
        }
    }else {
        echo '이미지가 존재하지 않습니다. ';
    }

    echo '저장 완료';
?>