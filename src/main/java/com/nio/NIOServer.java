package com.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by zhaozhengzeng on 2016/12/27.
 */
public class NIOServer {
    private int bufferSize = 4096;
    private int port = 9997;
    private ServerSocketChannel serverSocketChannel;
    private int flat = 0;
    private ByteBuffer sendBuffer = ByteBuffer.allocate(bufferSize);
    private ByteBuffer receivebuffer = ByteBuffer.allocate(bufferSize);
    private Selector selector;

    public NIOServer(int port) throws IOException {
        this.port = port;
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.configureBlocking(false);
        //为socketServer绑定端口
        this.serverSocketChannel.socket().bind(new InetSocketAddress(this.port));
        //打开selector,并且将在服务端channl上注册该selector和相关事件
        this.selector = Selector.open();
        //注册可以进行接收客户端请求事件
        this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server Start at " + this.port);
    }

    //监听,检测selector上事件的变化，有则处理
    private void listener() throws IOException {
        while (true) {
            this.selector.select();
            Set<SelectionKey> selectionKeySet = this.selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeySet.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                process(selectionKey);
                iterator.remove();
            }

        }
    }

    //处理请求
    private void process(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = null;
        SocketChannel client = null;
        String receiveText;
        String sendText;
        int count = 0;
        //服务端是否准备好客户端连接进来
        if (key.isValid() && key.isAcceptable()) {
            serverSocketChannel = (ServerSocketChannel) key.channel();
            client = serverSocketChannel.accept();
            client.configureBlocking(false);
            System.out.println("client has conneted !");
            //将客户端注册到selector
            client.register(this.selector, key.OP_READ);
        } else if (key.isValid() && key.isReadable()) {
            client = (SocketChannel) key.channel();
            //read from client
            this.receivebuffer.clear();

            try {
                count = client.read(this.receivebuffer);
                receiveText = new String(receivebuffer.array(), 0, count);
                System.out.println("read " + receiveText + "from client");
                client.register(this.selector, key.OP_WRITE);
            } catch (IOException e) {
                System.out.println("client is closed !");
                key.cancel();
            }

        } else if (key.isValid() && key.isWritable()) {
            client = (SocketChannel) key.channel();
            this.sendBuffer.clear();
            sendText = "write info " + this.flat + " to client";
            this.sendBuffer.put(sendText.getBytes());
            //将缓冲区设置为可可读状态
            this.sendBuffer.flip();
            try {
                client.write(this.sendBuffer);
                System.out.println("Server send " + sendText + " to client ");
                client.register(this.selector, key.OP_READ);
            } catch (IOException e) {
                System.out.println("client is closed!");
                key.cancel();
            }

        }
    }

    public static void main(String[] args) {
        try {
            NIOServer nioServer = new NIOServer(8881);
            nioServer.listener();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
