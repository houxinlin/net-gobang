package com.h.game;

import com.h.game.msg.MessageHandler;
import com.h.game.msg.RoomListMessage;

import javax.swing.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class GobangNet extends JFrame {
    private static List<MessageHandler> messageHandlers = new ArrayList<>();
    private static SocketChannel socketChannel;

    public List<MessageHandler> getMessageHandlers() {
        return messageHandlers;
    }

    public GobangNet() {
        messageHandlers.add(new RoomListMessage(this)); //房间返回
        new Thread(this::connectServer).start();
    }

    protected void connectServer() {
        try {
            if (socketChannel != null) return;
            socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 4567));
            System.out.println("服务器链接成功");
            socketChannel.write(Charset.defaultCharset().encode("list"));
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            while (socketChannel.read(byteBuffer) != -1) {
                byteBuffer.flip();
                byte[] data = new byte[byteBuffer.limit()];
                System.arraycopy(byteBuffer.array(), 0, data, 0, byteBuffer.limit());
                System.out.println(new String(data));
                for (MessageHandler messageHandler : messageHandlers) {
                    if (messageHandler.support(new String(data))) messageHandler.handler(new String(data));
                }
                byteBuffer.clear();
            }
            System.out.println("链接断开");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        try {
            socketChannel.write(Charset.defaultCharset().encode(msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
