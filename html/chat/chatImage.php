<?php 

    // 가입한 유저의 정보를 가져옴
    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    $imgUri = array();

    // 이미지가 존재하는지 확인
    if(isset($_FILES)){
        
        $numImages = count($_FILES);
        
        // 서버의 파일 경로
        $uploadDir = '/var/www/html/uploads/';

        $meeting_seq = $_POST['meeting_seq'];
        $user_seq = $_POST['user_seq'];
        $message_read = $_POST['message_read'];
        $message_date = $_POST['message_date'];

        // 순회면서 각 이미를 처리
        foreach ($_FILES as $key => $file) {
            if (is_uploaded_file($file['tmp_name'])) {
                $fileName = uniqid() . '_' . basename($file['name']);
                $uploadFile = $uploadDir . $fileName;

                // db에 저장될 경로
                $uploadDBDir = '/uploads/' . $fileName;

                // 이미지 저장
                move_uploaded_file($file['tmp_name'], $uploadFile);

                $stmt = $conn->prepare("INSERT INTO message_table (meeting_seq,
                user_seq, message_content, message_read, message_date)
                VALUES (:meeting_seq, :user_seq, 
                :message_content, :message_read, :message_date)");

                // 매게변수 바인딩
                $stmt->bindParam(':meeting_seq', $meeting_seq, PDO::PARAM_INT);
                $stmt->bindParam(':user_seq', $user_seq, PDO::PARAM_INT);
                $stmt->bindParam(':message_content', $uploadDBDir, PDO::PARAM_STR);
                $stmt->bindParam(':message_read', $message_read, PDO::PARAM_INT);
                $stmt->bindParam(':message_date', $message_date, PDO::PARAM_STR);
                
                $stmt->execute();

                $lastImgSeq = $conn->lastInsertId();

                $image = array(
                    'imgSeq' => $lastImgSeq,
                    'imgUrl' => $uploadDBDir
                );

                array_push($imgUri, $image);
                
            }
        }
    }

    echo json_encode($imgUri);

?>