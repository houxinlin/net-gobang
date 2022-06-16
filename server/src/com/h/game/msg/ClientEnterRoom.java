package com.h.game.msg;

import com.h.game.Main;
import com.h.game.room.Room;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class ClientEnterRoom extends MessageHandler {
    private List<Room> rooms = null;
    private static final String PREFIX = "EnterRoom";

    public ClientEnterRoom(List<Room> rooms) {
        this.rooms = rooms;
    }

    @Override
    public boolean support(String msg) {
        return msg.startsWith(PREFIX);
    }

    /**
     * 客户端进入房间
     *
     * @param msg
     * @param args
     */
    @Override
    public void handler(String msg, Object... args) {
        String roomName = msg.substring(PREFIX.length());
        SocketChannel socketChannel = ((SocketChannel) ((SelectionKey) args[0]).channel());

        String attachRoomName = (String) ((SelectionKey) args[0]).attachment();

        //如果已经关联了一个房间，将他清除
        if (attachRoomName != null)
            rooms.forEach((room -> {
                if (room.getRoomName().equals(attachRoomName)) {
                    if (room.getSocketChannelA() == socketChannel) room.setSocketChannelA(null);
                    if (room.getSocketChannelB() == socketChannel) room.setSocketChannelB(null);
                    room.setState(room.getState() - 1);
                }
            }));
        //将这个Socket添加附加数据，以关联房间号
        ((SelectionKey) args[0]).attach(roomName);
        rooms.forEach((room -> {
            if (room.getRoomName().equals(roomName)) {
                room.setState(room.getState() + 1); //房间状态+1,同时代表人数
                //设置双方socket
                if (room.getSocketChannelA() == null) {
                    room.setSocketChannelA(socketChannel);
                } else {
                    room.setSocketChannelB(socketChannel);
                }
                //如果到达了两个人
                if (room.getState() == 2) consult(room);

            }
        }));
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
}
