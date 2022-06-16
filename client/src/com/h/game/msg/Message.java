package com.h.game.msg;

public enum Message {
    ENTER_ROOM("EnterRoom"),
    PIECE("Piece");
    private String messageName;

    Message(String name) {
        this.messageName = name;
    }

    public String getMessageName() {
        return messageName;
    }
}
