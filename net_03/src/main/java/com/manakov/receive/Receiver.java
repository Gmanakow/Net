package com.manakov.receive;

import com.manakov.Node;
import com.manakov.message.Message;
import com.manakov.send.Sender;

import java.io.IOException;
import java.net.*;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class Receiver {
    private Timer timer;
    private TimerTask timerTask;

    private Node node;

    private DatagramSocket socket = null;
    private DatagramPacket packet = null;
    private byte[] buffer = null;

    public Receiver(Node node) throws SocketException {
        this.timer = new Timer();
        this.timerTask = new TimerTask() {
            @Override
            public void run() {
                receive();
            }
        };

        this.node = node;

        this.socket = new DatagramSocket(node.portNumber);
        this.socket.setSoTimeout(1000);
        this.buffer = new byte[256];
        packet = new DatagramPacket(buffer, buffer.length);
    }

    public void startReceiving(){
        this.timer.schedule(timerTask, 0);
    }

    public void receive(){
        while(true){
            try{
                socket.receive(packet);

                int rand = ThreadLocalRandom.current().nextInt(0, 99);
                if (rand < node.lossPercent) continue;

                Message message = new Message(packet.getData());
                InetSocketAddress sourceISAddress = new InetSocketAddress(
                        InetAddress.getByName(message.sourceAddress),
                        message.port
                );

                switch (message.type){
                    case (0) : // textMessage received
                        node.sendResponse(message, sourceISAddress);
                        node.printText(message);
                        node.sendText(message, sourceISAddress);
                        break;
                    case (1) : //  a new child is saying "hi" to his root
                        node.sendResponse(message, sourceISAddress);
                        node.acceptChild(sourceISAddress);
                        break;
                    case (2) : // node is getting a new backUp
                        node.sendResponse(message, sourceISAddress);
                        String data[] = message.data.split(" ");
                        node.backUpAddress = new InetSocketAddress(
                                InetAddress.getByName(data[0]),
                                Integer.parseInt(data[1])
                        );
                        break;
                    case (3) : // got a backUpCall. answering
                        node.sendResponse(message, sourceISAddress);
                        node.sendBackUp(sourceISAddress);
                        break;
                    case (4) : // responce
                        node.table.remove(message.id);
                        break;
                }
            } catch (SocketTimeoutException e){
            } catch (IOException e){
                System.out.println("err while receiving");
                e.printStackTrace();
            }
        }
    }




}
