package com.manakov;

import java.awt.geom.Path2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Timer;
import java.util.TimerTask;

public class Receiver {
    private Socket socket = null;
    private Timer timer = null;
    private TimerTask timerTask = null;
    private int number = -1;

    public Receiver(Socket socket, int number) throws SocketException {
        this.socket = socket;
        this.socket.setSoTimeout(2*1000);
        this.timer = new Timer();
        this.number = number;
        this.timerTask = new TimerTask() {
            @Override
            public void run() {
                receive();
                timer.purge();
            }
        };

    }

    public void startReceiving(){
        timer.schedule(timerTask, 0);
    }

    public void receive(){
        System.out.println("receiving :" + number);
        Counter counter = null;
        FileOutputStream outputStream = null;
        InputStream inputStream = null;

        try{
            inputStream = this.socket.getInputStream();
            outputStream = null;
            byte[] buffer = new byte[8];

            int nameLength = readNumber(inputStream).intValue();
            String fileName = readFileName(inputStream, nameLength);
            System.out.println(number + " fileName + " + fileName);

            double fileLength = readNumber(inputStream);
            System.out.println(number + " fileLength " + fileLength );

            int count = 0;

            File file = new File(fileName);
            Path path = Paths.get("uploads" + File.separator + " "  + number + " " + fileName);
            try{
                outputStream = new FileOutputStream(path.toFile());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            CountData countData = new CountData();
            counter = new Counter(countData, number);

            counter.start();
            while(true) {
                if ((count = inputStream.read(buffer)) > -1) {
                    countData.add(count);
                    outputStream.write(buffer, 0, count);
                } else {
                    System.out.println("finished receiving : " + number);
                    counter.stop();
                    outputStream.flush();
                    outputStream.close();
                    break;
                }
            }

        } catch (SocketTimeoutException | SocketException e) {
            System.out.println("lost connection to client :" + number);
            counter.stop();
            try {
                inputStream.close();
                outputStream.flush();
                outputStream.close();
            } catch (IOException ex){
                System.out.println("got error while closing : " + number);
                e.printStackTrace();
            }
        } catch (Exception e){
            System.out.println("got error while receiving : " + number);
            counter.stop();
            outputStream = null;
            inputStream = null;
            e.printStackTrace();
        }
    }

    private Long readNumber(InputStream inputStream) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byte[] buffer = new byte[8];

        if (inputStream.read(buffer) > 0) {
            byteBuffer.put(buffer);
            byteBuffer.clear();
            return byteBuffer.getLong();
        } else {
            throw new IOException("err while getting data : " + number);
        }
    }

    private String readFileName(InputStream inputStream, int nameLength) throws IOException{
        byte[] buffer = new byte[nameLength];
        if (!(inputStream.read(buffer) > 0)) throw new IOException("err while reading fileName : " + number);
        return new String(buffer);
    }
}
