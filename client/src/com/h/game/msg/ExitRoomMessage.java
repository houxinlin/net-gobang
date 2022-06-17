package com.h.game.msg;

import com.h.game.ui.panel.GobangMainPanelUI;

/**
 * 退出房间
 */
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
        /**
         * 这里是表示对手退出房间，则重置棋盘
         */
        gobangMainPanelUI.opponentExit();
    }
}
