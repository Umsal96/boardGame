<?php 

    // 가입한 유저의 정보를 가져옴
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    // post 로 전송된 데이터를 변수에 담음
    $userId = $_POST['userId'];
    $meetingId = $_POST['meetingId'];
    $boardTitle = $_POST['boardTitle'];
    $boardContent = $_POST['boardContent'];
    $boardType = $_POST['boardType'];

    // 쿼리 작성
    $stmt = $conn->prepare("INSERT INTO meeting_board (user_seq, meeting_seq,
        board_title, board_content, board_type, board_create_date)
        VALUES (:user_seq, :meeting_seq,
        :board_title, :board_content, :board_type, DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i'))");

    // 메개변수 바인딩
    $stmt->bindParam(':user_seq', $userId, PDO::PARAM_INT);
    $stmt->bindParam(':meeting_seq', $meetingId, PDO::PARAM_INT);
    $stmt->bindParam(':board_title', $boardTitle, PDO::PARAM_STR);
    $stmt->bindParam(':board_content', $boardContent, PDO::PARAM_STR);
    $stmt->bindParam(':board_type', $boardType, PDO::PARAM_STR);
    
    // 쿼리 실행
    $stmt->execute();

    $lastMeetingSeq = $conn->lastInsertId();

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
                $stmt1 = $conn->prepare("INSERT INTO image_table (image_url, image_place, board_seq,
                image_create, image_order) VALUES (:image_url, :image_place, 
                :board_seq, DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i'), :image_order)");

                // 메게변수 바인딩
                $stmt1->bindParam(':image_url', $uploadDBDir, PDO::PARAM_STR);
                $stmt1->bindParam(':image_place', $imagePlace, PDO::PARAM_INT);
                $stmt1->bindParam(':board_seq', $lastMeetingSeq, PDO::PARAM_INT);
                $stmt1->bindParam(':image_order', $imageOrder, PDO::PARAM_INT);

                $result1 = $stmt1->execute();

                if (!$result1) {
                    print_r($stmt1->errorInfo());
                }

                // Increment the image order for the next image
                $imageOrder++;
            }
        }
    }else {
            echo '이미지가 존재하지 않습니다. ';
    }

    echo '저장 완료';
    

?>