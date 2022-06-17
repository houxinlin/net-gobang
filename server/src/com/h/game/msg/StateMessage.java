package com.h.game.msg;

import com.h.game.room.Room;

import java.nio.channels.SelectionKey;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StateMessage extends MessageHandler {
    private Map<String, Integer> waitMap = new ConcurrentHashMap<>();
    private List<Room> rooms = null;

    public StateMessage(List<Room> rooms) {
        this.rooms = rooms;
    }

    @Override
    public boolean support(String msg) {
        return msg.startsWith("again");
    }


    /**
     * 协商 开始游戏
     *
     * @param room
     */
    private void consult(Room room) {
        try {
            System.out.println("房间:" + room.getRoomName() + " 开始游戏");
            room.getSocketChannelA().write(Charset.defaultCharset().encode(createConsultMessage("consult-start", room.getFirst())));
            room.getSocketChannelB().write(Charset.defaultCharset().encode(createConsultMessage("consult-start", room.getFirst() == 0 ? 1 : 0)));
        } catch (Exception e) {
        }
    }

    private String createConsultMessage(String action, int first) {
        return action + first;
    }

    @Override
    public void handler(String msg, SelectionKey selectionKey) {
        String roomName = (String) selectionKey.attachment();
        waitMap.put(roomName, waitMap.getOrDefault(roomName, 0) + 1);
        if (waitMap.get(roomName) == 2) {
            rooms.forEach((room -> {
                if (Integer.parseInt(msg.substring("again".length())) == 1) room.setFirst(room.getFirst() == 0 ? 1 : 0);
                if (room.getRoomName().equals(roomName)) {
                    consult(room);
                    waitMap.remove(roomName);
                }
            }));
        }
    }
}
