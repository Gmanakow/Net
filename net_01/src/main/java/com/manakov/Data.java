package com.manakov;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Data {

    private static Data instance;

    public static Data getInstance(){
        if (instance == null){
            instance = new Data();
        }
        return instance;
    }


    private int port = -1;
    public int getPort() throws IllegalStateException {
        if (port == -1) throw new IllegalStateException("port was not determined");
        else return port;
    }
    public void setPort(int port){
        this.port = port;
    }

    private InetAddress address = null;
    public InetAddress getAddress() throws IllegalStateException{
        if (address == null) throw new IllegalStateException("address was not determined");
        else return address;
    }
    public void setAddress(String address) throws UnknownHostException {
        this.address = InetAddress.getByName(address);
    }

    private static final byte[] byteBuffer = new byte[0];
    public byte[] getByteBuffer(){
        return this.byteBuffer;
    }

    private static final int period = 2000;
    public int getPeriod(){
        return this.period;
    }
}
