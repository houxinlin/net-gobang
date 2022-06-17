package com.h.game.ui;

import com.h.game.msg.Message;
import com.h.game.ui.panel.GobangMainPanelUI;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sendMessage(Message.EXIT_ROOM.getMessageName());
            }
        });
    }

}
