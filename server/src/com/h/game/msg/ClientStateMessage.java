package com.h.game.msg;

import com.google.gson.Gson;
import com.h.game.room.Room;
import com.h.game.server.GobangServerSocket;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Logger;

public class ClientStateMessage extends MessageHandler {
    private static final Logger log = Logger.getLogger(ClientStateMessage.class.getName());
    private List<Room> rooms = null;
    private static final String ENTER_PREFIX = "EnterRoom";
    private static final String EXIT_PREFIX = "ExitRoom";

    private GobangServerSocket serverSocket;

    public ClientStateMessage(List<Room> rooms, GobangServerSocket gobangServerSocket) {
        this.rooms = rooms;
        this.serverSocket = gobangServerSocket;
    }

    @Override
    public boolean support(String msg) {
        return msg.startsWith(ENTER_PREFIX) || msg.startsWith(EXIT_PREFIX);
    }

    /**
     * 客户端进入房间
     *
     * @param msg
     * @param selectionKey
     */
    @Override
    public void handler(String msg, SelectionKey selectionKey) {
        if (msg.startsWith(ENTER_PREFIX)) handlerEnter(msg, selectionKey);
        if (msg.startsWith(EXIT_PREFIX)) handlerExit(msg, selectionKey);
    }

    private void handlerExit(String msg, SelectionKey selectionKey) {
        String attachRoomName = (String) (selectionKey).attachment();
        exitRoom(attachRoomName, ((SocketChannel) selectionKey.channel()));
        selectionKey.attach(null);
        log.info("客户端退出房间" + attachRoomName);
    }

    /**
     * 清空Socket数据
     *
     * @param attachRoomName
     * @param socketChannel
     */
    public void exitRoom(String attachRoomName, SocketChannel socketChannel) {
        rooms.forEach((room -> {
            if (room.getRoomName().equals(attachRoomName)) {

                room.setState(room.getState() - 1);
                try {
                    if (room.getSocketChannelA() != null && room.getSocketChannelA() != socketChannel)
                        room.getSocketChannelA().write(Charset.defaultCharset().encode("ExitRoom"));
                    if (room.getSocketChannelB() != null && room.getSocketChannelB() != socketChannel)
                        room.getSocketChannelB().write(Charset.defaultCharset().encode("ExitRoom"));

                    if (room.getSocketChannelA() == socketChannel) room.setSocketChannelA(null);
                    if (room.getSocketChannelB() == socketChannel) room.setSocketChannelB(null);

                    notifyClientRefreshRoomList();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    /**
     * 进入房间
     *
     * @param msg
     * @param selectionKey
     */
    private void handlerEnter(String msg, SelectionKey selectionKey) {
        String roomName = msg.substring(ENTER_PREFIX.length());
        SocketChannel socketChannel = ((SocketChannel) selectionKey.channel());

        String attachRoomName = (String) (selectionKey).attachment();
        //如果已经关联了一个房间，将他清除
        if (attachRoomName != null) exitRoom(attachRoomName, socketChannel);

        //将这个Socket添加附加数据，以关联房间号
        selectionKey.attach(roomName);
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
                notifyClientRefreshRoomList();
            }
        }));
        log.info("客户端进入房间"+roomName);
    }

    private void notifyClientRefreshRoomList() {
        StringBuilder stringBuilder = new StringBuilder();
        serverSocket.broadcast(stringBuilder.append("rooms").append(new Gson().toJson(rooms)).toString());
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
