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
                                    msgTimer.schedule(
                                            new CustomTask(node.table.get(it), socket), 0
                                    );
                                    if (node.table.get(it).type == 4) {
                                        node.table.remove(it);
                                    }
                                }
                        );
                    }
                }, 500, 1000
        );
    }

    public void sendHi(){
        Message message = new Message(
                1,
                "",
                node.address,
                node.portNumber,
                node.parentAddress.getAddress().getHostName(),
                node.parentAddress.getPort()
        );
        node.table.put(message.id, message);
    }

    public void sendBackUp(InetSocketAddress backUp, InetSocketAddress dest) {
        String backUpString = backUp.getHostString();
        backUpString = backUpString.concat(" " + backUp.getPort());

        Message message =  new Message(
                2,
                backUpString,
                node.address,
                node.portNumber,
                dest.getAddress().getHostName(),
                dest.getPort()
        );

        node.table.put(message.id, message);
    }

    public void requestBackUp(InetSocketAddress isa){
        Message message = new Message(
                3,
                "",
                node.address,
                node.portNumber,
                isa.getAddress().getHostName(),
                isa.getPort()
        );
        node.table.put(message.id, message);
    }

    public void sendText(Message message, InetSocketAddress dest) {
        Message actMessage = new Message(
                0,
                message.data,
                message.sourceAddress,
                message.port,
                dest.getAddress().getHostName(),
                dest.getPort()
        );

        node.table.put(actMessage.id, actMessage);
    }

    public void sendResponse(Message message, InetSocketAddress isa){
        Message resMessage = new Message(
                4,
                "",
                node.address,
                node.portNumber,
                isa.getAddress().getHostName(),
                isa.getPort(),
                message.id
        );
        node.table.put(resMessage.id, resMessage);
    }


}
