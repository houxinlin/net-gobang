package com.h.game.msg;

import com.h.game.room.Room;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.List;

public abstract class MessageHandler {
    private List<Room> rooms = null;

    public MessageHandler(List<Room> rooms) {
        this.rooms = rooms;
    }

    public Room getRoomByName(String roomName) {
        for (Room room : this.rooms) {
            if (room.getRoomName().equals(roomName)) return room;
        }
        return null;
    }

    /**
     * 向客户端发送数据
     *
     * @param socketChannel
     * @param msg
     */
    public void sendMessage(SocketChannel socketChannel, String msg) {
        try {
            socketChannel.write(Charset.defaultCharset().encode(msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取房间列表
     *
     * @return
     */
    public List<Room> getRooms() {
        return rooms;
    }

    /**
     * 判断是否支持此消息类型
     *
     * @param msg
     * @return
     */
    public abstract boolean support(String msg);

    /**
     * 处理消息
     *
     * @param msg
     * @param selectionKey
     */
    public abstract void handler(String msg, SelectionKey selectionKey);
}
