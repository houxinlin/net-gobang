package com.h.game.msg;

import com.h.game.room.Room;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.List;

public class PieceMessage extends MessageHandler {
    private static final String PREFIX = "Piece";
    private List<Room> rooms = null;

    public PieceMessage(List<Room> rooms) {
        this.rooms = rooms;
    }

    @Override
    public boolean support(String msg) {
        return msg.startsWith(PREFIX);
    }

    @Override
    public void handler(String msg, SelectionKey selectionKey) {
        SocketChannel socketChannel = ((SocketChannel) selectionKey.channel());
        String roomName = (String) selectionKey.attachment();
        //吧这个数据转发给这个房间里的socket
        rooms.forEach((room -> {
            try {
                if (room.getRoomName().equals(roomName)) {
                    if (room.getSocketChannelA() != socketChannel
                            && room.getSocketChannelA() != null)
                        room.getSocketChannelA().write(Charset.defaultCharset().encode(msg));

                    if (room.getSocketChannelB() != socketChannel
                            && room.getSocketChannelB() != null)
                        room.getSocketChannelB().write(Charset.defaultCharset().encode(msg));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
}
