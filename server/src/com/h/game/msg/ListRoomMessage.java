package com.h.game.msg;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.h.game.room.Room;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.List;

public class ListRoomMessage extends MessageHandler {
    private List<Room> rooms = null;

    public ListRoomMessage(List<Room> rooms) {
        this.rooms = rooms;
    }


    @Override
    public boolean support(String msg) {
        return msg.equals("list");
    }

    @Override
    public void handler(String msg, Object... args) {
        SocketChannel socketChannel = ((SocketChannel) ((SelectionKey) args[0]).channel());
        StringBuilder stringBuffer = new StringBuilder();
        try {
            socketChannel.write(Charset.defaultCharset().encode(stringBuffer.append("rooms").append(JSON.toJSONString(rooms)).toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
