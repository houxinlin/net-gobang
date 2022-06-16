package com.h.game.server;

import com.h.game.msg.ClientEnterRoom;
import com.h.game.msg.ListRoomMessage;
import com.h.game.msg.MessageHandler;
import com.h.game.msg.PieceMessage;
import com.h.game.room.Room;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GobangServerSocket {
    private static final int PORT = 4567;
    private static List<MessageHandler> messageHandlers = new ArrayList<>();

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private final List<Room> rooms = new ArrayList<>();

    public GobangServerSocket() {
        try {
            //创建10个房间
            for (int i = 0; i < 10; i++) rooms.add(new Room((i + 1) + ""));
            messageHandlers.add(new ClientEnterRoom(rooms)); //处理客户端进入房间
            messageHandlers.add(new ListRoomMessage(rooms));//处理客户端请求房间列表
            messageHandlers.add(new PieceMessage(rooms)); //处理棋子的信息
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(PORT));
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            for (; ; ) loop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlerAccept(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    /**
     * 从客户端读取数据
     *
     * @param selectionKey
     * @throws IOException
     */
    private void handlerRead(SelectionKey selectionKey) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        if (((SocketChannel) selectionKey.channel()).read(byteBuffer) == -1) {
            selectionKey.channel().close();
            return;
        }
        byteBuffer.flip();
        byte[] data = new byte[byteBuffer.limit()];
        System.arraycopy(byteBuffer.array(), 0, data, 0, byteBuffer.limit());
        for (MessageHandler messageHandler : messageHandlers) {
            if (messageHandler.support(new String(data))) messageHandler.handler(new String(data), selectionKey);
        }
        byteBuffer.clear();
    }

    private void loop() {
        try {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                if (selectionKey.isAcceptable()) handlerAccept(selectionKey);
                if (selectionKey.isReadable()) handlerRead(selectionKey);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
