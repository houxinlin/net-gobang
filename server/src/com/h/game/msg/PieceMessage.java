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
    public void handler(String msg, Object... args) {
        SocketChannel socketChannel = ((SocketChannel) ((SelectionKey) args[0]).channel());
        String[] data = msg.substring(PREFIX.length()).split(":");
        String roomName = (String) ((SelectionKey) args[0]).attachment();
        //吧这个数据转发给这个房间里的socket
        rooms.forEach((room -> {
            try {
                if (room.getRoomName().equals(roomName)) {
                    System.out.println("装发数据:"+msg);
                    if (room.getSocketChannelA() != socketChannel
                            && room.getSocketChannelA() != null)
                    {
                        System.out.println("发送给A");
                        room.getSocketChannelA().write(Charset.defaultCharset().encode(msg));
                    }

                    if (room.getSocketChannelB() != socketChannel
                            && room.getSocketChannelB() != null)
                    {
                        System.out.println("发送给B");
                        room.getSocketChannelB().write(Charset.defaultCharset().encode(msg));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
}
