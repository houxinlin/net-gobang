package com.h.game.ui;

import com.h.game.msg.Message;
import com.h.game.ui.panel.GobangMainPanelUI;

public class GobangMainUI extends BaseGobangUI {
    private String roomName;

    public GobangMainUI(String roomName) {
        this.roomName = roomName;
        sendMessage(Message.ENTER_ROOM.getMessageName() + roomName);
    }

    @Override
    protected void init() {
        super.init();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
        this.getContentPane().add(new GobangMainPanelUI(this));
    }

}
