package com.example.boardgame.socket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class clientSocket {

    private Socket socket;

    public interface ConnectionCallback {
        void onConnectionEstablished();
        void onConnectionFailed(Exception e);
    }

    public clientSocket(){
        socket = null;
    }

    public void establishConnection(ConnectionCallback callback) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (socket == null || socket.isClosed()) {
                        socket = new Socket();
                        socket.connect(new InetSocketAddress("3.38.213.196", 9999));
                        callback.onConnectionEstablished();
                    }
                } catch (IOException e) {
                    callback.onConnectionFailed(e);
                }
            }
        }).start();
    }

    public void waitingClientSocket(int TransmitUserSeq, int ReceiveUserSeq , String result) {
        establishConnection(new ConnectionCallback() {
            @Override
            public void onConnectionEstablished() {
                try {
                    OutputStream os = socket.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(os);

                    byte[] bytes = (TransmitUserSeq + "," + ReceiveUserSeq + "," + result).getBytes("UTF-8");

                    // 메시지 길이를 먼저 전송
                    dos.writeInt(bytes.length);
                    // 메시지 내용을 전송
                    dos.write(bytes, 0, bytes.length);
                    // 출력 스트림 비우기
                    dos.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
//                    closeSocket(); // 메시지 전송 후 소켓 닫기
                }
            }

            @Override
            public void onConnectionFailed(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void closeSocket(){
        if(socket != null && !socket.isClosed()){
            try{
                socket.close();
            } catch (IOException e){
                e.printStackTrace();
            } finally {
                socket = null;
            }
        }
    }
}