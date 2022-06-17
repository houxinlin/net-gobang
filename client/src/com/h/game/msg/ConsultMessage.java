package com.h.game.msg;

import com.h.game.ui.panel.GobangMainPanelUI;

/**
 * 协商消息
 */
public class ConsultMessage extends MessageHandler {
    private GobangMainPanelUI gobangMainPanelUI;

    public ConsultMessage(GobangMainPanelUI gobangMainPanelUI) {
        this.gobangMainPanelUI = gobangMainPanelUI;
    }

    @Override
    public boolean support(String msg) {
        return msg.startsWith("consult-");
    }

    @Override
    public void handler(String msg, Object... args) {
        /**
         * 如果是协商开始
         */
        if (msg.startsWith("consult-start"))
            gobangMainPanelUI.start(Integer.parseInt(msg.substring("consult-start".length())));
    }
}
