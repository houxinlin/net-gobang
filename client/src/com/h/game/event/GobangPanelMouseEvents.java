package com.h.game.event;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class GobangPanelMouseEvents extends MouseAdapter {
    private final GobangPanelMoushCallback gobangPanelMoushCallback;

    public GobangPanelMouseEvents(GobangPanelMoushCallback gobangPanelMoushCallback) {
        this.gobangPanelMoushCallback = gobangPanelMoushCallback;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (gobangPanelMoushCallback != null) gobangPanelMoushCallback.mouseReleased(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (gobangPanelMoushCallback != null) gobangPanelMoushCallback.mouseMoved(e);
    }
}
