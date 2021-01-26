package com.manakov;

import com.manakov.message.Message;
import com.manakov.receive.Receiver;
import com.manakov.send.CustomTask;
import com.manakov.send.Sender;
import com.manakov.util.IncorrectNumberOfArgumentsException;

import java.io.Console;
import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.*;

public class Node {
    public int nodeType = 0; // 0 - root, 1 - child
    public String nodeName = "";

    public int lossPercent = -1;

    public String address = "";
    public int portNumber = -1;

    public InetSocketAddress parentAddress = null;
    public InetSocketAddress backUpAddress = null;

    public ArrayList<InetSocketAddress> list = null;

    private Console input = null;
    private PrintStream output = null;

    private Sender sender = null;
    private Receiver receiver = null;

    public void setSender(Sender sender){
        this.sender = sender;
    }
    public void setReceiver(Receiver receiver){
        this.receiver = receiver;
    }

    public Hashtable<UUID, Message> table = new Hashtable<>();

    public Node(String[] args) throws IncorrectNumberOfArgumentsException, UnknownHostException, IOException {
        if (!((args.length == 3) || (args.length==5))){
            throw new IncorrectNumberOfArgumentsException("wrong number of arguments");
        }

        nodeName = args[0];

        portNumber = Integer.parseInt(args[1]);
        address = InetAddress.getByName("localhost").getHostName();

        lossPercent = Integer.parseInt(args[2]);
        if (lossPercent < 0) lossPercent = 0;
        if (lossPercent > 99) lossPercent = 99;

        if (args.length == 5){
            nodeType = 1;
            parentAddress = new InetSocketAddress(
                    InetAddress.getByName(args[3]),
                    Integer.parseInt(args[4])
            );
        }

        list = new ArrayList<>();

        this.output = System.out;
        this.input = System.console();
    }

    public void startReceiving(){
        this.receiver.startReceiving();;
    }
    public void startReading(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String line = "";
                while(true) {
                    line = input.readLine();
                    sendText(new Message(0, line, address, portNumber, "", -1), null);
                }
            }
        }, 0);
    }

    public void sendBackUp(InetSocketAddress isaDest){
        if (this.parentAddress != null) {
            sender.sendBackUp(parentAddress, isaDest);
        } else if (list.size() > 1) {
            sender.sendBackUp(list.get(0), isaDest);
        }
    }
    public void requestBackUp(InetSocketAddress isa){
        sender.requestBackUp(isa);
    }

    public void acceptChild(InetSocketAddress acceptedChild){
        this.list.forEach(
                (o) -> {
                    if (compareIsa(o, acceptedChild)) {
                        return;
                    }
                }
        );
        this.list.add(acceptedChild);
    }
    public void printText(Message message){
        this.output.println(message.data);
    }
    public void sendText(Message message, InetSocketAddress source){

        message.sourceAddress = address;
        message.port = portNumber;

        for (InetSocketAddress isa : list){
            if (source == null) {
                sender.sendText(message, isa);
            } else {
                if (!compareIsa(isa, source)) {
                    sender.sendText(message, isa);
                }
            }
        }
        if (parentAddress != null) {
            if (source == null ) {
                sender.sendText(message, parentAddress);
            } else {
                if (!compareIsa(parentAddress, source)) {
                    sender.sendText(message, parentAddress);
                }
            }
        }
    }

    public boolean compareIsa(InetSocketAddress first, InetSocketAddress second) {
        return (first.getPort() == second.getPort() && first.getHostName().equals(second.getHostName()));
    }

    public void sendResponse(Message message, InetSocketAddress isa){
        sender.sendResponse(message, isa);
    }
}
