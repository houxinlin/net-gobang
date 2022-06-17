package com.h.game.msg;

import java.nio.channels.SelectionKey;

public abstract class MessageHandler {
    public abstract boolean support(String msg);

    public abstract void handler(String msg, SelectionKey  selectionKey);
}
