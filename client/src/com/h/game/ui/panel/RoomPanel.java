package com.h.game.ui.panel;

import com.h.game.event.GobangPanelMouseEvents;
import com.h.game.event.GobangPanelMoushCallback;
import com.h.game.ui.GobangMainUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class RoomPanel  extends JPanel {
    public RoomPanel(String roomName,int state) {
        this.setLayout(new BorderLayout());
        this.add(new JLabel( new ImageIcon(RoomPanel.class.getResource("/resource/home.png"),roomName)),BorderLayout.CENTER);
        this.add(new JLabel(roomName,SwingConstants.CENTER),BorderLayout.SOUTH);
        addMouseListener(new GobangPanelMouseEvents(new GobangPanelMoushCallback() {
            @Override
            public void mouseReleased(MouseEvent e) {
                new GobangMainUI(roomName);
            }
        }));
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

    }
}
