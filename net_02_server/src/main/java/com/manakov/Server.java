package com.manakov;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Hello world!
 *
 */
public class Server
{
    public static void main( String[] args )
    {
        int port = 0;
        int count = 0;

        System.out.println(
                "starting server"
        );

        try{
            port = Integer.parseInt(args[0]);
        } catch (Exception e){
            System.out.println("incorrect arguments");
            System.exit(1);
        }

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                new Receiver(serverSocket.accept(), count++).startReceiving();
            }
        } catch (IOException e){
            System.out.println("recieving err");
            System.exit(1);
        }
    }
}
