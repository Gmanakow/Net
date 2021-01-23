package com.manakov;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;

/**
 * Hello world!
 *
 */
public class Client
{
    public static void main( String[] args )
    {
        FileChooser fileChooser = null;
        try{
            fileChooser = new FileChooser(args[0]);
        } catch (IOException e){
            System.out.println("failed to open a file");
            e.printStackTrace();
            System.exit(1);
        }

        Sender sender = null;
        try {
            sender = new Sender(
                args[1], Integer.parseInt(args[2]), fileChooser
            );
            sender.connect();
            sender.startSending();
        } catch (ConnectException e){
            System.out.println("failed to connect");
            e.printStackTrace();
            System.exit(1);
        }
        catch (IOException e){
            System.out.println("failed to create a Sender");
            e.printStackTrace();
            System.exit(1);
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("wrong number of arguments");
            System.exit(1);
        }
    }
}
