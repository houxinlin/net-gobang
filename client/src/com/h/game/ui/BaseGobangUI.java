package com.h.game.ui;

import com.h.game.GobangNet;
import com.h.game.msg.MessageHandler;
import com.h.game.msg.RoomListMessage;

import javax.swing.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseGobangUI extends GobangNet {
    public BaseGobangUI() {
        init();
    }

    protected void init() {
        this.setSize(850, 850);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
}
