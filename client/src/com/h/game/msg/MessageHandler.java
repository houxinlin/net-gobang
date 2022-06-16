package com.h.game.msg;

public abstract class MessageHandler {
  public   abstract boolean support(String msg);

    public abstract void handler(String msg, Object... args);
}
