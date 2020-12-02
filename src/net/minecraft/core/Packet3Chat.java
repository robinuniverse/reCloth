package net.minecraft.core;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet3Chat extends Packet {

    public String message;

    public Packet3Chat() {
    }

    public Packet3Chat(String s) {
        message = s;
    }

    public void readPacketData(DataInputStream datainputstream) throws IOException {
        message = datainputstream.readUTF();
    }

    public void writePacketData(DataOutputStream dataoutputstream) throws IOException {
        dataoutputstream.writeUTF(message);
    }

    public void processPacket(NetHandler nethandler) {
        nethandler.handleChat(this);
    }

    public int getPacketSize() {
        return message.length();
    }
}
