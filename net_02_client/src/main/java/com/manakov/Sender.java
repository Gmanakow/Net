package com.manakov;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class Sender {
    private Timer timer = null;
    private TimerTask timerTaskSend = null;

    private Socket socket = null;
    private InetSocketAddress inetSocketAddress = null;
    private InetAddress inetAddress = null;

    private int portNumber;

    private FileChooser fileChooser = null;

    private OutputStream outputStream = null;

    public Sender(String serverAddress, int portNumber, final FileChooser fileChooser) throws IOException{
        this.timer = new Timer();
        this.fileChooser = fileChooser;

        this.timerTaskSend = new TimerTask() {
            @Override
            public void run(){
                sendInfo();
                sendData();
                System.exit(0);
            }
        };

        this.socket = new Socket();
        this.socket.setSoTimeout(10000);

        this.inetAddress = InetAddress.getByName(serverAddress);
        this.portNumber = portNumber;

        this.inetSocketAddress = new InetSocketAddress(inetAddress, portNumber);
    }

    public void startSending(){

        timer.schedule(timerTaskSend, 0);
    }

    public void connect() throws IOException {
        socket.connect(
            inetSocketAddress
        );
        this.outputStream = socket.getOutputStream();
    }

    private void sendData(){
        byte[] buffer = new byte[8];
        int bytesRead = 0;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = fileChooser.getFileInputStream();
        } catch (IOException e){
            System.out.println("err while opening");
            System.exit(1);
        }
        while (true){
            try{
                if ((bytesRead = fileInputStream.read(buffer)) != -1){
                    outputStream.write(buffer, 0, bytesRead);
                } else {
                    outputStream.flush();
                    outputStream.close();
                    System.out.println("finished sending");
                    return;
                }
            } catch (IOException e){
                System.out.println("err while sending");
                e.printStackTrace();
            }
        }
    }

    private void sendInfo() {
        String fileName = fileChooser.getFileName();
        Long fileLength = fileChooser.getFileLength();

        try {
            sendFileName(fileName);
            sendFileLength(fileLength);
        } catch (IOException e) {
            System.out.println("err while sending info");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void sendFileName(String fileName) throws IOException{

        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.putLong(fileName.getBytes().length);

        outputStream.write(byteBuffer.array());
        outputStream.write(fileName.getBytes());

    }

    private void sendFileLength(Long fileLength) throws IOException{

        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.putLong(fileLength);

        outputStream.write(byteBuffer.array());
    }
}
