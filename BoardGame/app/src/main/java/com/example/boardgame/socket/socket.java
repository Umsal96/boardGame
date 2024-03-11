package com.example.boardgame.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class socket {

    public void waitingClientSocket(int userSeq, String result){
        Socket socket = null;

        try{
            socket = new Socket();
            System.out.println("\n[ Request ... ]");
            socket.connect(new InetSocketAddress("3.38.213.196", 9999));
            System.out.println("\n[ Success ... ]");

            byte[] bytes = null;
            String message = null;

            // 서버로 정보를 보내기
            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            // 서버에서 전송되온 정보 받기
            InputStream is = socket.getInputStream();
            DataInputStream dis = new DataInputStream(is);

            // 메시지를 읽고 출력
            Thread messageReceiver = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        while(true){
                            int receiveLength = dis.readInt();
                            if(receiveLength > 0){
                                // 읽을 데이터를 저장할 배열 생성
                                byte receiveByte[] = new byte[receiveLength];
                                dis.readFully(receiveByte, 0, receiveLength);
                                // 읽어온 데이터를 문자열로 변환하여 출력
                                String receivedMessage = new String(receiveByte, "UTF-8");
                                System.out.println(receivedMessage);
                            }
                        }

                    }catch (IOException e){
                        e.printStackTrace();
                    }

                }
            });
        // 메시지 수신 스레드 시작
        messageReceiver.start();

        while (true){
            bytes = (userSeq + ":" + result).getBytes("UTF-8");

            // 메시지 길이를 먼저 전송
            dos.writeInt(bytes.length);
            // 메시지 내용을 전송
            dos.write(bytes, 0, bytes.length);
            // 출력 스트림 비우기
            dos.flush();
        }

        } catch (Exception e){
            e.printStackTrace();
        }
        if(!socket.isClosed()){
            try{
                socket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
