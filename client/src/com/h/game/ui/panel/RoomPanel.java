package com.h.game.ui.panel;

import com.h.game.event.GobangPanelMouseEvents;
import com.h.game.event.GobangPanelMouseCallback;
import com.h.game.ui.GobangMainUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * 单个房间面板
 */
public class RoomPanel extends JPanel {
    public RoomPanel(String roomName, int state) {
        this.setLayout(new BorderLayout());
        this.add(new JLabel(new ImageIcon(RoomPanel.class.getResource("/resource/home.png"), roomName)), BorderLayout.CENTER);
        this.add(new JLabel(roomName + "(" + state + ")", SwingConstants.CENTER), BorderLayout.SOUTH);
        addMouseListener(new GobangPanelMouseEvents(new GobangPanelMouseCallback() {
            @Override
            public void mouseReleased(MouseEvent e) {
                //如果房间人数没满，则进入房间
                if (state <= 1) new GobangMainUI(roomName);
            }
        }));
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

    }
}
