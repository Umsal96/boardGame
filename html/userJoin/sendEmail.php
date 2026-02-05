<?php
    use PHPMailer\PHPMailer\PHPMailer;
    // use PHPMailer\PHPMailer\Exception; // 없어도 되는것

    // PHPMailer 라이브러리를 불러옵니다.
    // require '../lib/vendor/phpmailer/phpmailer/src/PHPMailer.php'; // 없어도 되는것
    // require '../lib/vendor/phpmailer/phpmailer/src/Exception.php'; // 없어도 되는것
    require '../vendor/autoload.php';

    $email = $_GET['email'];

    $mail = new PHPMailer();
    $mail->isSMTP();                          // SMTP 사용 설정
    $mail->Host = '#';           // SMTP 서버 설정
    $mail->SMTPAuth = true;                   // SMTP 인증을 사용함
    $mail->Username = '#';     // SMTP 계정
    $mail->Password = '#';             // SMTP 계정 비밀번호
    $mail->SMTPSecure = 'ssl';                // SSL을 사용함
    $mail->Port = #;                        // TCP 포트
    
    $mail->setFrom('#', '#', 'utf-8');
    $mail->addAddress($email, '유져', 'utf-8');
    
    $mail->Subject = '=?utf-8?B?' . base64_encode('인증 메일입니다.') . '?=';
    $authCode = rand(100000, 999999);
    $content = "인증코드가 도착했습니다." . $authCode;
    
    $mail->CharSet = 'UTF-8';
    $mail->ContentType = 'text/plain';
    $mail->Body = $content;
    
    if (!$mail->send()) {
        echo '2';
    } else {
        echo $authCode;
    }
    
?>
