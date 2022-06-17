package com.h.game.server;

public class GobangServer {
    public void start(int port) {
        new GobangServerSocket(port);
    }
}
