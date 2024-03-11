<?php 

    // 데이터 베이스 연결
    include '../Utility/db_connect.php';

    $phone = $_GET['phone'];

    // 모듈화된 db 연결
    $conn = connectToDatabase();

    // 핸드폰 번로 겹치는 데이터를 찾음
    $stmt = $conn->prepare("SELECT * FROM user_info WHERE user_phone = :phone");
    $stmt->bindParam(':phone', $phone, PDO::PARAM_STR);

    $stmt->execute();

    // 전화번호로 검색했을때 나온 결과물을 result 객체에 넣는다.
    $result = $stmt->fetch(PDO::FETCH_ASSOC);

    if($result){
        $response = array(
            "status" => "1",
            "email" => $result["user_email"]
        );
        echo json_encode($response);
    } else {
        $response = array(
            "status" => "2",
            "email" => "해당하는 사용자가 없습니다."
        );
        echo json_encode($response);
    }
        
    $stmt->close();
    $conn = null;

?>