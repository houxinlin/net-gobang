package com.h.game.event;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GobangPanelMouseEvents extends MouseAdapter {
    private final GobangPanelMouseCallback gobangPanelMouseCallback;

    public GobangPanelMouseEvents(GobangPanelMouseCallback gobangPanelMouseCallback) {
        this.gobangPanelMouseCallback = gobangPanelMouseCallback;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (gobangPanelMouseCallback != null) gobangPanelMouseCallback.mouseReleased(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (gobangPanelMouseCallback != null) gobangPanelMouseCallback.mouseMoved(e);
    }
}
