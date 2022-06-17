package com.h.game.room;

import com.alibaba.fastjson.annotation.JSONField;

import java.nio.channels.SocketChannel;

public class Room {
    @JSONField(serialize = false)
    private SocketChannel socketChannelA;
    @JSONField(serialize = false)
    private SocketChannel socketChannelB;
    private int state=0;
    private String roomName;
    private int first = 0;

    public Room(String roomName) {
        this.roomName = roomName;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public SocketChannel getSocketChannelA() {
        return socketChannelA;
    }

    public void setSocketChannelA(SocketChannel socketChannelA) {
        this.socketChannelA = socketChannelA;
    }

    public SocketChannel getSocketChannelB() {
        return socketChannelB;
    }

    public void setSocketChannelB(SocketChannel socketChannelB) {
        this.socketChannelB = socketChannelB;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
