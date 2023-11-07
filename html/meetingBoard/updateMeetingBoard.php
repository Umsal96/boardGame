<?php 

    // 오류가 발생시 출력됨
    error_reporting(E_ALL); // 모든 php 오류를 표시하도록 설정합니다.
    ini_set('display_errors', '1'); // 오류를 화면에 표시하도록 설정합니다.

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    // post 로 전송된 데이터를 변수에 담음
    $boardId = $_POST['boardId'];
    $boardTitle = $_POST['boardTitle'];
    $boardContent = $_POST['boardContent'];
    $boardType = $_POST['boardType'];
    
    if (isset($_POST['imageOrder'])) {
        $imageOrder = $_POST['imageOrder'];
        echo 'imageOrder 가 있습니다.';
        // 이미지 처리 코드
    } else {
        $imageOrder = ''; // 이미지 순서가 없는 경우 빈 문자열 또는 다른 기본값 설정
        // 이미지 없는 경우 처리 코드
    }

    $stmt = $conn->prepare("UPDATE meeting_board 
    SET board_title = :board_title, board_content = :board_content, 
        board_type = :board_type, board_modified_date = DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i')
        WHERE board_seq = :board_seq");

    $stmt->bindParam(':board_title', $boardTitle, PDO::PARAM_STR);
    $stmt->bindParam(':board_content', $boardContent, PDO::PARAM_STR);
    $stmt->bindParam(':board_type', $boardType, PDO::PARAM_STR);
    $stmt->bindParam(':board_seq', $boardId, PDO::PARAM_INT);

    // 쿼리 실행
    if($stmt->execute()){
        echo '수정 성공';
    } else{
        echo '수정 실패';
    }

    // 이미지가 존재하는지 확인
    if(!empty($_FILES)) {
        echo '이미지가 존재합니다.';
        $imageOrder = $imageOrder + 1;
        $numImages = count($_FILES);
        echo '전송된 이미지의 개수: ' . $numImages;

        // 서버의 파일 경로 지정
        $uploadDir = '/var/www/html/uploads/';

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
                $stmt1->bindParam(':board_seq', $boardId, PDO::PARAM_INT);
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