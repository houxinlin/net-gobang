package com.h.game.event;

import java.awt.event.MouseEvent;

public interface GobangPanelMouseCallback {
    default void mouseMoved(MouseEvent e){}

    default void  mouseReleased(MouseEvent e){}
}
