package net.minecraft.core;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 


public class ServerCommand {

    public final String command;
    public final ICommandListener commandListener;
    public ServerCommand(String s, ICommandListener icommandlistener) {
        command = s;
        commandListener = icommandlistener;
    }
}