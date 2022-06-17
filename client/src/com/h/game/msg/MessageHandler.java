package com.h.game.msg;

public abstract class MessageHandler {
    /**
     * 受支持的消息类新
     * @param msg
     * @return
     */
    public abstract boolean support(String msg);

    /**
     * 处理消息
     * @param msg
     * @param args
     */
    public abstract void handler(String msg, Object... args);
}
