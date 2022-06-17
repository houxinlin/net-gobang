package com.h.game.msg;

import com.h.game.ui.panel.GobangMainPanelUI;

public class ExitRoomMessage extends MessageHandler {
    private GobangMainPanelUI gobangMainPanelUI;

    public ExitRoomMessage(GobangMainPanelUI gobangMainPanelUI) {
        this.gobangMainPanelUI = gobangMainPanelUI;
    }

    @Override
    public boolean support(String msg) {
        return msg.startsWith("ExitRoom");
    }

    @Override
    public void handler(String msg, Object... args) {
        gobangMainPanelUI.opponentExit();
    }
}
