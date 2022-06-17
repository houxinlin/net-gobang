package com.h.game.msg;

import com.google.gson.Gson;
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
    public void handler(String msg, SelectionKey selectionKey) {
        SocketChannel socketChannel = ((SocketChannel) selectionKey.channel());
        StringBuilder stringBuffer = new StringBuilder();
        try {
            socketChannel.write(Charset.defaultCharset().encode(stringBuffer.append("rooms").append(new Gson().toJson(rooms)).toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
