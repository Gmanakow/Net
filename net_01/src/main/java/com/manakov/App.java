package com.manakov;

import java.io.IOException;


public class App
{
    public static void main( String[] args ) {
        Sender sender = null;
        Receiver receiver = null;

        if (args.length != 2){
            System.out.println("wrong number of arguments");
            System.exit(1);
        }

        try{

            String multicastAddress = args[0];
            int portNumber = Integer.parseInt(args[1]);

            Data.getInstance().setAddress(multicastAddress);
            Data.getInstance().setPort(portNumber);

            sender = new Sender();
            sender.startTransmitting();

            receiver = new Receiver();
            receiver.startReceiving();
            System.out.println("success");

        } catch (IOException e){
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
}
