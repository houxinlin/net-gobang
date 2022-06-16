package com.h.game.msg;

import com.h.game.ui.panel.GobangMainPanelUI;

public class ConsultMessage extends MessageHandler {
    private GobangMainPanelUI gobangMainPanelUI;

    public ConsultMessage(GobangMainPanelUI gobangMainPanelUI) {
        this.gobangMainPanelUI = gobangMainPanelUI;
    }

    public void setGobangMainPanelUI(GobangMainPanelUI gobangMainPanelUI) {
        this.gobangMainPanelUI = gobangMainPanelUI;
    }

    @Override
    public boolean support(String msg) {
        return msg.startsWith("consult-");
    }

    @Override
    public void handler(String msg, Object... args) {
        System.out.println(msg);
        if (msg.startsWith("consult-start")) gobangMainPanelUI.start(Integer.parseInt(msg.substring("consult-start".length())));
    }
}
