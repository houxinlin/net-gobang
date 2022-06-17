package com.h.game.msg;

import com.google.gson.Gson;
import com.h.game.room.Room;
import com.h.game.server.GobangServerSocket;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ClientStateMessage extends MessageHandler {
    private static final Logger log = Logger.getLogger(ClientStateMessage.class.getName());
    private static final String ENTER_PREFIX = "EnterRoom";
    private static final String EXIT_PREFIX = "ExitRoom";
    private static final String AGAIN_PREFIX = "again";
    private GobangServerSocket serverSocket;
    /**
     * 二次开始游戏时，等待的人数，当对应房间数量为2人时，服务端发送开始信息到房间中的两人
     */
    private Map<String, Integer> waitMap = new ConcurrentHashMap<>();

    public ClientStateMessage(List<Room> rooms, GobangServerSocket serverSocket) {
        super(rooms);
        this.serverSocket = serverSocket;
    }


    @Override
    public boolean support(String msg) {
        return msg.startsWith(ENTER_PREFIX) || msg.startsWith(EXIT_PREFIX) || msg.startsWith(AGAIN_PREFIX);
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
        if (msg.startsWith(AGAIN_PREFIX)) handlerAgain(msg, selectionKey);
    }

    /**
     * 处理下一局
     *
     * @param msg
     * @param selectionKey
     */
    private void handlerAgain(String msg, SelectionKey selectionKey) {
        String roomName = (String) selectionKey.attachment();
        /**
         * 当客户端结束本轮对局时候，单击窗口后发送again消息，服务端只有在waitMap中value为2的时候向两个客户端发送重新开始指令
         */
        waitMap.put(roomName, waitMap.getOrDefault(roomName, 0) + 1);
        if (waitMap.get(roomName) == 2) {
            Room room = getRoomByName(roomName);
            //如果后面是1,则表示先手的输了，切换两人颜色，设置对方先手
            if (Integer.parseInt(msg.substring("again".length())) == 1) room.setFirst(room.getFirst() == 0 ? 1 : 0);
            //协商 开始下一场对局
            consult(room);
            //状态移除
            waitMap.remove(roomName);
        }
    }

    /**
     * 处理客户端退房间
     *
     * @param msg
     * @param selectionKey
     */
    private void handlerExit(String msg, SelectionKey selectionKey) {
        String attachRoomName = (String) (selectionKey).attachment();
        exitRoom(attachRoomName, ((SocketChannel) selectionKey.channel()));
        selectionKey.attach(null);
        log.info("客户端退出房间" + attachRoomName);
    }

    /**
     * 客户端退出房间，清空房间中Socket数据
     *
     * @param attachRoomName
     * @param socketChannel
     */
    public void exitRoom(String attachRoomName, SocketChannel socketChannel) {
        Room room = getRoomByName(attachRoomName);
        room.setState(room.getState() - 1);
        try {
            if (room.getSocketChannelA() != null && room.getSocketChannelA() != socketChannel)
                room.getSocketChannelA().write(Charset.defaultCharset().encode("ExitRoom"));
            if (room.getSocketChannelB() != null && room.getSocketChannelB() != socketChannel)
                room.getSocketChannelB().write(Charset.defaultCharset().encode("ExitRoom"));

            if (room.getSocketChannelA() == socketChannel) room.setSocketChannelA(null);
            if (room.getSocketChannelB() == socketChannel) room.setSocketChannelB(null);

            //通知所有客户端进行房间信息刷新
            notifyClientRefreshRoomList();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        Room room = getRoomByName(roomName);
        room.setState(room.getState() + 1); //房间状态+1,同时代表人数
        //设置双方socket
        if (room.getSocketChannelA() == null) {
            room.setSocketChannelA(socketChannel);
        } else {
            room.setSocketChannelB(socketChannel);
        }
        //如果到达了两个人,开始协商，进行游戏
        if (room.getState() == 2) consult(room);
        notifyClientRefreshRoomList();
        log.info("客户端进入房间" + roomName);
    }

    /**
     * 刷新客户端房间列表
     */
    private void notifyClientRefreshRoomList() {
        StringBuilder stringBuilder = new StringBuilder();
        serverSocket.broadcast(stringBuilder.append("rooms").append(new Gson().toJson(getRooms())).toString());
    }

    /**
     * 协商 开始游戏
     *
     * @param room
     */
    private void consult(Room room) {
        try {
            log.info("房间:" + room.getRoomName() + " 开始游戏");
            room.getSocketChannelA().write(Charset.defaultCharset().encode(createConsultMessage("consult-start", room.getFirst())));
            room.getSocketChannelB().write(Charset.defaultCharset().encode(createConsultMessage("consult-start", room.getFirst() == 0 ? 1 : 0)));
        } catch (Exception e) {
        }
    }

    private String createConsultMessage(String action, int first) {
        return action + first;
    }
}
