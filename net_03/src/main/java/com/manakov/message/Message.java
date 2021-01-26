package com.manakov.message;

import java.nio.ByteBuffer;
import java.util.UUID;

public class Message {
    public int type = -1;

    public String sourceAddress = "";
    public int port = -1;

    public String destAddress = "";
    public int destPort = -1;

    public String data = "";
    public UUID id = null;

    public Message(int type, String data, String sourceAddress, int port, String destAddress, int destPort){
        this.type = type;

        this.sourceAddress = sourceAddress;
        this.port = port;

        this.destAddress = destAddress;
        this.destPort = destPort;

        this.data = data;
        this.id = UUID.randomUUID();
    }

    public Message(int type, String data, String sourceAddress, int port, String destAddress, int destPort, UUID id){
        this.type = type;
        this.sourceAddress = sourceAddress;
        this.port = port;
        this.destAddress = destAddress;
        this.destPort = destPort;
        this.data = data;
        this.id = id;
    }

    public Message(byte[] bytes){
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        byteBuffer.put(bytes);

        byteBuffer.rewind();
        this.type = byteBuffer.getInt();

        int addressLength = byteBuffer.getInt();
        byte[] addressBytes = new byte[addressLength];
        for (int i = 0; i< addressLength; i++){
            addressBytes[i] = byteBuffer.get();
        }
        this.sourceAddress = new String(addressBytes);

        this.port = byteBuffer.getInt();

        int l = byteBuffer.getInt();
        byte[] dataBytes = new byte[l];
        for (int i = 0; i< l; i++){
            dataBytes[i] = byteBuffer.get();
        }
        this.data = new String(dataBytes);

        int idl = byteBuffer.getInt();
        dataBytes = new byte[idl];
        for (int i = 0; i< idl; i++){
            dataBytes[i] = byteBuffer.get();
        }
        String uuid = new String(dataBytes);
        this.id = UUID.fromString(uuid);
    }

    public byte[] toByte(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        byteBuffer.putInt(type);

        byteBuffer.putInt(sourceAddress.length());
        byteBuffer.put(sourceAddress.getBytes());

        byteBuffer.putInt(port);

        byteBuffer.putInt(data.length());
        byteBuffer.put(data.getBytes());

        byte[] bytes = this.id.toString().getBytes();

        byteBuffer.putInt(bytes.length);
        byteBuffer.put(bytes);

        return byteBuffer.array();
    }
}
