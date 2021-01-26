package com.manakov;

import com.manakov.message.Message;
import com.manakov.receive.Receiver;
import com.manakov.send.Sender;
import com.manakov.util.IncorrectNumberOfArgumentsException;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class Client
{
    public static void main( String[] args )
    {
        Node node = null;
        Sender sender = null;
        Receiver receiver = null;
        try {
            node = new Node(args);

            sender = new Sender(node);
            receiver = new Receiver(node);

            node.setReceiver(receiver);
            node.setSender(sender);

        } catch (IncorrectNumberOfArgumentsException | IOException e){
            e.printStackTrace();
            System.exit(1);
        }

        node.startReceiving();
        node.startReading();

        switch (node.nodeType) {
            case (0) :
                // the node is root, do nothing;
                break;
            case (1) :
                // the node is a child, say hi to your root;
                sender.sendHi();
                node.requestBackUp(node.parentAddress);
                break;
        }




    }




}
