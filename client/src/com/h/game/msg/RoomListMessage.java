package com.h.game.msg;

import com.alibaba.fastjson.JSON;
import com.h.game.room.Room;
import com.h.game.ui.GobangRoomUI;

import javax.swing.*;
import java.util.List;

public class RoomListMessage extends MessageHandler {
    private JFrame jFrame;

    public RoomListMessage(JFrame jFrame) {
        this.jFrame = jFrame;
    }

    @Override
    public boolean support(String msg) {
        return msg.startsWith("rooms");
    }

    @Override
    public void handler(String msg, Object... args) {
        if (jFrame != null && jFrame instanceof GobangRoomUI) {
            List<Room> rooms = JSON.parseArray(msg.substring("rooms".length()), Room.class);
            rooms.forEach(room -> {
                ((GobangRoomUI) jFrame).addRoom(room.getRoomName(),room.getState());
            });
        }
    }
}
