<?php 

    error_reporting(E_ALL);
    ini_set('display_errors', '1');

    // 데이터 베이스 연결
    include './Utility/db_connect.php';

    // 모둘화된 db 연결을 가져옴
    $conn = connectToDatabase();

    $files = glob('./abroad/*.json');

    foreach($files as $file){
        $jsonContent = file_get_contents($file);

        $jsonDataArray = json_decode($jsonContent, true);

        foreach ($jsonDataArray as $jsonData){
            if(isset($jsonData['name'])){
                // echo $jsonData['name'];
    
                $stmt = $conn->prepare("INSERT INTO ready_game(ready_name)
                    VALUES (:ready_name)");
    
                $stmt->bindParam(':ready_name', $jsonData['name'], PDO::PARAM_STR);
    
                $stmt->execute();
            }
        }
    }
?>