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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 网络管理，所有界面都需要网络，所有界面都继承自他
 */
public abstract class GobangNet extends JFrame {
    /**
     * 消息处理器
     */
    private static List<MessageHandler> messageHandlers = new ArrayList<>();
    private static SocketChannel socketChannel;
    private static final List<String> HOST = Collections.unmodifiableList(Arrays.asList("localhost", "www.houxinlin.com"));

    public List<MessageHandler> getMessageHandlers() {
        return messageHandlers;
    }

    public GobangNet() {
        messageHandlers.add(new RoomListMessage(this)); //房间返回
        new Thread(this::connectServer).start();
    }

    protected void connectServer() {
        if (socketChannel != null) return;
        HOST.forEach((host) -> {
            try {
                socketChannel = SocketChannel.open(new InetSocketAddress(host, 8086));
                System.out.println("服务器链接成功"+host);
                socketChannel.write(Charset.defaultCharset().encode("list"));
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                while (socketChannel.read(byteBuffer) != -1) {
                    byteBuffer.flip();
                    byte[] data = new byte[byteBuffer.limit()];
                    System.arraycopy(byteBuffer.array(), 0, data, 0, byteBuffer.limit());
                    for (MessageHandler messageHandler : messageHandlers) {
                        if (messageHandler.support(new String(data))) messageHandler.handler(new String(data));
                    }
                    byteBuffer.clear();
                }
                System.out.println("链接断开");
            } catch (IOException e) {
                System.err.println(host+"连接失败");
            }
        });


    }

    public void sendMessage(String msg) {
        try {
            socketChannel.write(Charset.defaultCharset().encode(msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
