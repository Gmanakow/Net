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

                int rand = ThreadLocalRandom.current().nextInt(1, 99);
                if (rand < node.lossPercent) continue;


                Message message = new Message(packet.getData());
                InetSocketAddress sourceISAddress = new InetSocketAddress(
                        InetAddress.getByName(message.sourceAddress),
                        message.port
                );

//                System.out.println("got " +  message.type + " " + message.response + " from " + message.port);

                switch (message.type) {
                    case (0): // textMessage received
                        if (message.response == 0) {
                            node.sendResponse(message, sourceISAddress);
                            node.printText(message);
                            node.sendText(message, sourceISAddress);
                        } else {
                            node.table.remove(message.id);
                        }
                        break;
                    case (1): //  a new child is saying "hi" to his root
                        if (message.response == 0) {
                            node.sendResponse(message, sourceISAddress);
                            String prevData[] = message.data.split(" ");
                            node.acceptChild(sourceISAddress, prevData);
                        } else {
                            node.setParentAddress(sourceISAddress);
                            node.table.remove(message.id);
                        }
                        break;
                    case (2): // got a backUpCall. answering
                        if (message.response == 0){
                            node.sendResponse(message, sourceISAddress);
                            node.sendBackUp(sourceISAddress);
                        } else {
                            node.table.remove(message.id);
                        }
                        break;
                    case (3): // node is getting a new backUp
                        if (message.response == 0) {
                            node.sendResponse(message, sourceISAddress);
                            String backUpData[] = message.data.split(" ");
                            if (backUpData.length == 2) {
                                node.backUpAddress = new InetSocketAddress(
                                        InetAddress.getByName(backUpData[0]),
                                        Integer.parseInt(backUpData[1])
                                );
                            } else {
                                node.backUpAddress = null;
                            }
                        } else {
                            node.table.remove(message.id);
                        }
                        break;
                    case (4): // message, that a certain node is not responding.
                        if (message.response == 0) {
                            node.sendResponse(message, sourceISAddress);
                            String missingData[] = message.data.split(" ");
                            node.checkMissing(missingData[0], Integer.parseInt(missingData[1]));
                            node.sendNodeMissing(missingData[0], Integer.parseInt(missingData[1]), sourceISAddress);

                        } else {
                            node.table.remove(message.id);
                        }
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
