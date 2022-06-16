package com.h.game.ui;

import com.h.game.ui.panel.RoomPanel;

import javax.swing.*;
import java.awt.*;

public class GobangRoomUI extends BaseGobangUI {
    @Override
    protected void init() {
        super.init();
        this.setSize(400, 400);
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 20));
        this.setLocationRelativeTo(null);
        this.setVisible(true);

    }


    public void addRoom(String roomName,int state) {
        RoomPanel roomPanel = new RoomPanel(roomName,state);
        roomPanel.setSize(200, 100);
        add(roomPanel);
        SwingUtilities.updateComponentTreeUI(this);

    }
}
