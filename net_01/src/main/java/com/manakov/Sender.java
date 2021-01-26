package com.manakov;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

public class Sender {

    private Timer timer = null;
    DatagramSocket datagramSocket = null;
    DatagramPacket datagramPacket = null;
    TimerTask timerTask = null;

    public Sender(){
        timer = new Timer(false);

        try {
            datagramSocket = new DatagramSocket();
        } catch (SocketException e){
            System.out.println("socket exception occurred when creating a Sender socket");
            System.out.println(e.getMessage());
        }

        try{
            datagramPacket = new DatagramPacket(
                Data.getInstance().getByteBuffer(),
                Data.getInstance().getByteBuffer().length,
                Data.getInstance().getAddress(),
                Data.getInstance().getPort()
            );
        } catch (IllegalStateException e){
            System.out.println(e.getMessage());
        }

        timerTask =  new TimerTask() {
            @Override
            public void run() {
                try {
                    datagramSocket.send(datagramPacket);
                } catch (IOException e){
                    System.out.println("err while sending");
                    System.out.println(e.getMessage());
                }
            }
        };
    }

    public void startTransmitting(){
        timer.schedule(timerTask, 0, Data.getInstance().getPeriod());
    }

}
