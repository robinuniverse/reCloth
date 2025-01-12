package net.minecraft.core;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

class ThreadLoginVerifier extends Thread {

    final Packet1Login loginPacket; /* synthetic field */
    final NetLoginHandler loginHandler; /* synthetic field */

    ThreadLoginVerifier(NetLoginHandler netloginhandler, Packet1Login packet1login) {
        loginHandler = netloginhandler;
        loginPacket = packet1login;
    }

    public void run() {
        PropertyManager propertyManager = new PropertyManager(new File("server.properties"));

        try {
            String serverId = NetLoginHandler.getServerId(loginHandler);
            URL url = null;
            url = new URL((new StringBuilder()).append("http://session.minecraft.net/game/checkserver.jsp?user=").append(loginPacket.username).append("&serverId=").append(serverId).toString());

            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
            String s1 = bufferedreader.readLine();
            bufferedreader.close();
            System.out.println((new StringBuilder()).append("THE REPLY IS ").append(s1).toString());
            if (s1.equals("YES")) {
                NetLoginHandler.setLoginPacket(loginHandler, loginPacket);
            } else {
                loginHandler.kickUser("Failed to verify username!");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
