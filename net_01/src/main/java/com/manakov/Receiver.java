package com.manakov;

import java.io.IOException;
import java.net.*;
import java.util.ConcurrentModificationException;
import java.util.Hashtable;
import java.util.Iterator;

public class Receiver {
    MulticastSocket socket = null;
    DatagramPacket datagramPacket = null;
    Hashtable<SocketAddress, Long> table = null;

    public Receiver() throws IOException {

        socket = new MulticastSocket(
                new InetSocketAddress(
                        Data.getInstance().getPort()
                )
        );

        socket.joinGroup(
                Data.getInstance().getAddress()
        );
        socket.setSoTimeout(
                Data.getInstance().getPeriod()
        );

        datagramPacket = new DatagramPacket(
                Data.getInstance().getByteBuffer(),
                0
        );

        table = new Hashtable<>();
    }

    public void startReceiving(){
        while(true){
            boolean needPrinting = false;
            try{
                socket.receive(datagramPacket);
                SocketAddress socketAddress = datagramPacket.getSocketAddress();
                if (!table.containsKey(socketAddress)) {
                    needPrinting = true;
                }
                table.put(socketAddress, System.currentTimeMillis());
            } catch (IOException e){
            }

            Iterator<SocketAddress> iterator = table.keySet().iterator();

                while(iterator.hasNext()){
                    SocketAddress address = iterator.next();
                    if (System.currentTimeMillis() - table.get(address) > Data.getInstance().getPeriod() * 2) {
                        iterator.remove();
                        needPrinting = true;
                    }
                }

                int i = 0;
                if (needPrinting) for (SocketAddress address : table.keySet()) {
                    System.out.println(address + " " + i++);
                }
        }
    }
}
