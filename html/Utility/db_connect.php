<?php

    function connectToDatabase(){
        // 데이터베이스 연결 설정
        $servername = "127.0.0.1"; // MySQL 서버 주소
        $username = "hong";
        $password = "1234";
        $dbname = "boardGame_db";

        try{
            // PDO 객체를 사용하여 데이버 베이스에 연결
            $conn = new PDO("mysql:host=$servername; dbname=$dbname; charset=utf8", $username, $password);

            // PDO 객체 설정
            $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

            // PDO 객체 설정
            return $conn;

        } catch(PDOException $e){
            // 연결 오류 발생 시 예외 처리
            die("Connection failed: " . $e->getMessage());
        }
    }
    
?>