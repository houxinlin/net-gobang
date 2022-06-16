package com.h.game.room;

import java.nio.channels.SocketChannel;

public class Room {
    private SocketChannel socketChannelA;
    private SocketChannel socketChannelB;
    private int state;
    private String roomName;

    public Room(String roomName) {
        this.roomName = roomName;
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
