package com.h.game.event;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class GobangPanelMouseEvents implements MouseListener, MouseMotionListener {
    private final GobangPanelMoushCallback gobangPanelMoushCallback;

    public GobangPanelMouseEvents(GobangPanelMoushCallback gobangPanelMoushCallback) {
        this.gobangPanelMoushCallback = gobangPanelMoushCallback;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (gobangPanelMoushCallback != null) gobangPanelMoushCallback.mouseReleased(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (gobangPanelMoushCallback != null) gobangPanelMoushCallback.mouseMoved(e);
    }
}
