<?php

session_start();

//전송할 핸드폰 번호를 여기에 넣는다


$phoneNum = $_GET["phone"];

// sms 보내기 추가
$sID = "#"; // 서비스 ID

$smsURL = "#";
$smsUri = "#";

$accKeyId = "#";   //인증키 id
$accSecKey = "#";  //secret key

$sTime = floor(microtime(true) * 1000);

$authNum = rand(100000, 999999);// 랜덤 인증 번호 생성

// The data to send to the API
$postData = array(
    'type' => 'SMS',
    'countryCode' => '82',
    'from' => '#', // 발신번호 (등록되어있어야함)
    'contentType' => 'COMM',
    'content' => "메세지 내용",
    'messages' => array(array('content' => "문자입니다. 인증번호: ".$authNum, 'to' => $phoneNum))
);

$postFields = json_encode($postData) ;

$hashString = "POST {$smsUri}\n{$sTime}\n{$accKeyId}";


$dHash = base64_encode( hash_hmac('sha256', $hashString, $accSecKey, true) );

$header = array(
    // "accept: application/json",
    'Content-Type: application/json; charset=utf-8',
    'x-ncp-apigw-timestamp: '.$sTime,
    "x-ncp-iam-access-key: ".$accKeyId,
    "x-ncp-apigw-signature-v2: ".$dHash
);

//curl은 다양한 프로토콜로 전송이 가능한 command line tool 이다
// Setup cURL
$ch = curl_init($smsURL);

curl_setopt_array($ch, array(   //옵션을 배열로 한번에 설정한다
    CURLOPT_POST => TRUE,
    CURLOPT_RETURNTRANSFER => TRUE,
    CURLOPT_HTTPHEADER => $header,
    CURLOPT_POSTFIELDS => $postFields
));

$response = curl_exec($ch);//설정된 옵션으로 실행한다

curl_close($ch);//chrl을 닫아준다

if (!empty($response)) {
    $responseData = json_decode($response, true); // Parse JSON response to an associative array
    if (isset($responseData['statusCode']) && $responseData['statusCode'] === '202') {
        $responseData['authCode'] = $authNum;
        echo json_encode($responseData);
    } else {
        echo "Failed to send SMS. Status code: " . $responseData['statusCode'];
        // Handle the error case and any other details as needed
    }
} else {
    echo "No response received from server.";
}

?>
