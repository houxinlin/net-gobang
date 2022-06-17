package com.h.game.msg;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.h.game.room.Room;
import com.h.game.ui.GobangRoomUI;

import javax.swing.*;
import java.util.List;

/**
 * 房间列表
 */
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
            ((GobangRoomUI) jFrame).clear();
            List<Room> rooms = new Gson().fromJson(msg.substring("rooms".length()), new TypeToken<List<Room>>() {}.getType());
            rooms.forEach(room -> {
                ((GobangRoomUI) jFrame).addRoom(room.getRoomName(), room.getState());
            });
        }
    }
}
