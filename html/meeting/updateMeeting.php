<?php
    // 오류가 발생시 출력됨
    error_reporting(E_ALL); // 모든 php 오류를 표시하도록 설정합니다.
    ini_set('display_errors', '1'); // 오류를 화면에 표시하도록 설정합니다.

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    $meeting_seq = $_POST['meeting_seq'];
    $meeting_name = $_POST['meeting_name'];
    $meeting_content = $_POST['meeting_content'];
    $meeting_members = $_POST['meeting_members'];
    $meeting_address = $_POST['meeting_address'];
    $meeting_place_name = $_POST['meeting_place_name'];
    $x = $_POST['x'];
    $y = $_POST['y'];

    // 이미지가 수정되었는지 확인 만약 2이면 수정되었음 만약 1이면 수정되지 않았음
    if($_POST['change'] == '2'){
        // 만약 이미지가 전송되지 않았다면 -> 이미지가 기본 이미지로 수정됨
        // 쿼리문 작성 meeting_table 에서 meeting_seq에 해당되는 uri를 가져옴
            // 만약 가져왔을떄 값이 없다면 그대로 유지 만약 있있다면 해당 이미지를 삭제
            $stmt = $conn->prepare("SELECT meeting_url FROM meeting_table WHERE meeting_seq = :meeting_seq");
            $stmt->bindParam(':meeting_seq', $meeting_seq, PDO::PARAM_INT);
            $stmt->execute();
            $previousImage = $stmt->fetchColumn();

        // 이미지가 전송되지 않았을때
        if(!isset($_FILES['image'])){
            $uploadDir = '/var/www/html';

            $uploadDBDir = null;
            
            if($previousImage && file_exists($uploadDir . $previousImage)){
                unlink($uploadDir . $previousImage);
            }

            // 쿼리문 작성 meeting_url 과 meeting_modified_date 를 수정
            $stmt1 = $conn->prepare("UPDATE meeting_table SET meeting_url = :meeting_url, meeting_name = :meeting_name,
                meeting_place_name = :meeting_place_name, meeting_content = :meeting_content, meeting_lat = :meeting_lat,
                meeting_lnt = :meeting_lnt, meeting_address = :meeting_address, meeting_members = :meeting_members,
                meeting_modified_date = NOW() WHERE meeting_seq = :meeting_seq");

            $stmt1->bindParam(':meeting_url' , $uploadDBDir, PDO::PARAM_STR);
            $stmt1->bindParam(':meeting_name', $meeting_name, PDO::PARAM_STR);
            $stmt1->bindParam(':meeting_place_name', $meeting_place_name, PDO::PARAM_STR);
            $stmt1->bindParam(':meeting_content', $meeting_content, PDO::PARAM_STR);
            $stmt1->bindParam(':meeting_lat', $y, PDO::PARAM_STR);
            $stmt1->bindParam(':meeting_lnt', $x, PDO::PARAM_STR);
            $stmt1->bindParam(':meeting_address', $meeting_address, PDO::PARAM_STR);
            $stmt1->bindParam(':meeting_members', $meeting_members, PDO::PARAM_INT);
            $stmt1->bindParam(':meeting_seq', $meeting_seq, PDO::PARAM_INT);
            $stmt1->execute();
            echo '1'; //성공
                
        // 전송되어온 이미지가 있을경우
        } else {
            // 서버의 파일 경로 지정
            $fileName = uniqid() . '_' . basename($_FILES['image']['name']);
            $uploadDir = '/var/www/html/uploads/'; // 이미지를 저장할 디렉터리 경로
            $uploadFile = $uploadDir . $fileName;

            // db에 저장될 경로 
            $uploadDBDir = '/uploads/' . $fileName;
            $preUploadDir = '/var/www/html';

            // 이전 이미지 삭제
            if ($previousImage && file_exists($preUploadDir . $previousImage)) {
                unlink($preUploadDir . $previousImage);
            }

            if (move_uploaded_file($_FILES['image']['tmp_name'], $uploadFile)) {
                // 쿼리문 작성 meeting_url 과 meeting_modified_date 를 수정
                $stmt2 = $conn->prepare("UPDATE meeting_table SET meeting_url = :meeting_url, meeting_name = :meeting_name,
                meeting_place_name = :meeting_place_name, meeting_content = :meeting_content, meeting_lat = :meeting_lat,
                meeting_lnt = :meeting_lnt, meeting_address = :meeting_address, meeting_members = :meeting_members,
                meeting_modified_date = NOW() WHERE meeting_seq = :meeting_seq");

                $stmt2->bindParam(':meeting_url' , $uploadDBDir, PDO::PARAM_STR);
                $stmt2->bindParam(':meeting_name', $meeting_name, PDO::PARAM_STR);
                $stmt2->bindParam(':meeting_place_name', $meeting_place_name, PDO::PARAM_STR);
                $stmt2->bindParam(':meeting_content', $meeting_content, PDO::PARAM_STR);
                $stmt2->bindParam(':meeting_lat', $y, PDO::PARAM_STR);
                $stmt2->bindParam(':meeting_lnt', $x, PDO::PARAM_STR);
                $stmt2->bindParam(':meeting_address', $meeting_address, PDO::PARAM_STR);
                $stmt2->bindParam(':meeting_members', $meeting_members, PDO::PARAM_INT);
                $stmt2->bindParam(':meeting_seq', $meeting_seq, PDO::PARAM_INT);
                $stmt2->execute();
                echo '1'; //성공
            }
        }
    // 이미지를 수정하지 않았다면 실행되는 코드
    }elseif ($_POST['change'] == '1') {
        $stmt3 = $conn->prepare("UPDATE meeting_table SET meeting_name = :meeting_name,
            meeting_place_name = :meeting_place_name, meeting_content = :meeting_content, meeting_lat = :meeting_lat,
            meeting_lnt = :meeting_lnt, meeting_address = :meeting_address, meeting_members = :meeting_members,
            meeting_modified_date = NOW() WHERE meeting_seq = :meeting_seq");

        $stmt3->bindParam(':meeting_name', $meeting_name, PDO::PARAM_STR);
        $stmt3->bindParam(':meeting_place_name', $meeting_place_name, PDO::PARAM_STR);
        $stmt3->bindParam(':meeting_content', $meeting_content, PDO::PARAM_STR);
        $stmt3->bindParam(':meeting_lat', $y, PDO::PARAM_STR);
        $stmt3->bindParam(':meeting_lnt', $x, PDO::PARAM_STR);
        $stmt3->bindParam(':meeting_address', $meeting_address, PDO::PARAM_STR);
        $stmt3->bindParam(':meeting_members', $meeting_members, PDO::PARAM_INT);
        $stmt3->bindParam(':meeting_seq', $meeting_seq, PDO::PARAM_INT);
        $stmt3->execute();
        echo '1'; //성공
    }
    
?>