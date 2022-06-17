package com.h.game;

import com.h.game.server.GobangServer;
import com.h.game.server.GobangServerSocket;


public class Main {

    public static void main(String[] args) {
        int port = args.length == 0 ? GobangServerSocket.PORT : Integer.parseInt(args[0]);
        new GobangServer().start(port);
    }
}
