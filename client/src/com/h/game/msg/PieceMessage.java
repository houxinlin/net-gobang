package com.h.game.msg;

import com.h.game.ui.panel.GobangMainPanelUI;

public class PieceMessage extends MessageHandler {
    private static final String PREFIX = "Piece";
    private GobangMainPanelUI gobangMainPanelUI;

    public PieceMessage(GobangMainPanelUI gobangMainPanelUI) {
        this.gobangMainPanelUI = gobangMainPanelUI;
    }

    public void setGobangMainPanelUI(GobangMainPanelUI gobangMainPanelUI) {
        this.gobangMainPanelUI = gobangMainPanelUI;
    }

    @Override
    public boolean support(String msg) {
        return msg.startsWith(PREFIX);
    }

    @Override
    public void handler(String msg, Object... args) {
        String[] data = msg.substring(PREFIX.length()).split(":");
        gobangMainPanelUI.setCellData(Integer.parseInt(data[0]),Integer.parseInt(data[1]),1);
    }
}
