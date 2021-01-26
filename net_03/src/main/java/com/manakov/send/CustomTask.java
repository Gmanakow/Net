package com.manakov.send;

import com.manakov.message.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TimerTask;

public class CustomTask extends TimerTask {
    private DatagramPacket datagramPacket;
    private DatagramSocket datagramSocket;

    private Message message;

    protected CustomTask(Message message, DatagramSocket datagramSocket) {
        super();

        this.message = message;

        byte[] buffer = new byte[256];
        buffer = message.toByte();
        this.datagramPacket = new DatagramPacket(buffer, buffer.length);

        try {
            this.datagramPacket.setAddress(InetAddress.getByName(message.destAddress));
            this.datagramPacket.setPort(message.destPort);
        } catch (UnknownHostException e){
            System.out.println(e.getMessage());
        }
        this.datagramSocket = datagramSocket;
    }

    @Override
    public void run() {
        try {
            this.datagramSocket.send(datagramPacket);
        } catch (IOException e){
            System.out.println("err while sending to " + datagramPacket.getAddress() + " " +datagramPacket.getPort());
            e.printStackTrace();
        }
    }

    @Override
    public boolean cancel() {
        return super.cancel();
    }

    @Override
    public long scheduledExecutionTime() {
        return super.scheduledExecutionTime();
    }
}
