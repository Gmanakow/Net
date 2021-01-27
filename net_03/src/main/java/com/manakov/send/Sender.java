package com.manakov.send;

import com.manakov.Node;
import com.manakov.message.Message;

import java.net.*;
import java.util.*;

public class Sender {
    private Node node;

    private DatagramSocket socket = null;
    private byte[] buffer = null;
    private Timer timer;
    private Timer msgTimer;

    public Sender(Node node) throws SocketException {
        this.node = node;

        this.socket = new DatagramSocket();
        this.buffer = new byte[256];

        this.timer = new Timer();
        this.msgTimer = new Timer();

        this.timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        ArrayList<UUID> ids = new ArrayList<>();
                        ids.addAll(node.table.keySet());
                        ids.forEach(
                                (it) -> {
//                                    System.out.println("send " +  node.table.get(it).type + " " + node.table.get(it).response + " to " + node.table.get(it).destPort);
                                    msgTimer.schedule(
                                            new CustomTask(node.table.get(it), socket), 0
                                    );
                                    node.table.get(it).counter++;
                                    if (node.table.get(it).response == 1){
                                        node.table.remove(it);
                                    } else {
                                        if (node.table.get(it).counter > 30) {
                                            System.out.println("lost connection to " + node.table.get(it).destPort);
                                            node.checkMissing(node.table.get(it).destAddress, node.table.get(it).destPort);
                                            node.sendNodeMissing(node.table.get(it).destAddress, node.table.get(it).destPort, null);
                                            node.table.remove(it);
                                        }
                                    }
                                }
                        );
                    }
                }, 100, 100
        );
    }

    public void sendText(Message message, InetSocketAddress dest) {
        Message actMessage = new Message(
                0,
                0,
                message.data,
                message.sourceAddress,
                message.port,
                dest.getAddress().getHostName(),
                dest.getPort()
        );

        node.table.put(actMessage.id, actMessage);
    }

    public void sendHi(InetSocketAddress inetSocketAddress, InetSocketAddress prev) {
        String data = "";
        if (prev != null){
            data = prev.getAddress().getHostName().concat(" " + prev.getPort());
        } else {
            data = "null";
        }

        Message message = new Message(
                1,
                0,
                data,
                node.address,
                node.portNumber,
                inetSocketAddress.getAddress().getHostName(),
                inetSocketAddress.getPort()
        );
        node.table.put(message.id, message);
    }

    public void requestBackUp(InetSocketAddress isa){
        Message message = new Message(
                2,
                0,
                "",
                node.address,
                node.portNumber,
                isa.getAddress().getHostName(),
                isa.getPort()
        );
        node.table.put(message.id, message);
    }

    public void sendBackUp(InetSocketAddress backUp, InetSocketAddress dest) {
        String backUpString = "";
        if (backUp != null) {
            backUpString = backUp.getAddress().getHostName().concat(" " + backUp.getPort());
        } else {
            backUpString = "null";
        }

        Message message =  new Message(
                3,
                0,
                backUpString,
                node.address,
                node.portNumber,
                dest.getAddress().getHostName(),
                dest.getPort()
        );

        node.table.put(message.id, message);
    }

    public void sendNodeMissing(String missingAddress, int missingPort, InetSocketAddress isa){
        Message missMessage = new Message(
                4,
                0,
                missingAddress.concat(" " + missingPort),
                node.address,
                node.portNumber,
                isa.getAddress().getHostName(),
                isa.getPort()
        );
        node.table.put(missMessage.id, missMessage);
    }

    public void sendResponse(Message message, InetSocketAddress isa){
        Message resMessage = new Message(
                message.type,
                1,
                message.data,
                node.address,
                node.portNumber,
                isa.getAddress().getHostName(),
                isa.getPort(),
                message.id
        );
        node.table.put(resMessage.id, resMessage);
    }


}
