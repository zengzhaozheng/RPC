package com.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by zhaozhengzeng on 2016/12/28.
 */
public class NIOClient {
    private SocketChannel client;
    private Selector selector;
    private int port;
    private String serverIP;
    private ByteBuffer sendBuffer = ByteBuffer.allocate(4096);
    private ByteBuffer receiveBuffer = ByteBuffer.allocate(4096);

    public void init(String serverIP, int port) throws IOException {
        this.serverIP = serverIP;
        this.port = port;
        this.client = SocketChannel.open();
        this.client.configureBlocking(false);
        this.selector = Selector.open();
        this.client.register(this.selector, SelectionKey.OP_CONNECT);
        this.client.connect(new InetSocketAddress(this.serverIP, this.port));
    }

    public void runSession() throws IOException {
        while (true) {
            this.selector.select();
            Set<SelectionKey> keySet = this.selector.selectedKeys();
            Iterator<SelectionKey> it = keySet.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                process(key);
                it.remove();
            }
        }


    }

    public void process(SelectionKey key) throws IOException {
        int count = 0;
        String receiveText;
        if (key.isValid() && key.isConnectable()) {
            System.out.println("client connect");
            this.client = (SocketChannel) key.channel();
            //判断此通道是否正在进行连接操作
            if (this.client.isConnectionPending()) {
                //完成套接字通道的连接过程。
                this.client.finishConnect();
                System.out.println("connect the server finished!");
                this.sendBuffer.clear();
                this.sendBuffer.put(" Hello,Server ".getBytes());
                this.sendBuffer.flip();
                this.client.write(this.sendBuffer);
                this.client.register(this.selector, key.OP_READ);
            }

        } else if (key.isValid() && key.isReadable()) {
            this.client = (SocketChannel) key.channel();
            this.receiveBuffer.clear();
            count = this.client.read(receiveBuffer);
            if (count > 0) {
                receiveText = new String(receiveBuffer.array(), 0, count);
                System.out.println("receive message from server:" + receiveText);
                this.client.register(this.selector, key.OP_WRITE);
            }
        } else if (key.isValid() && key.isWritable()) {
            this.client = (SocketChannel) key.channel();
            this.sendBuffer.clear();
            this.sendBuffer.put("the messsage that is sent by clent: helle world".getBytes());
            this.sendBuffer.flip();
            this.client.write(this.sendBuffer);
            this.client.register(this.selector, key.OP_READ);
        }


    }

    public static void main(String[] args) {
        NIOClient nioClient = new NIOClient();
        try {
            nioClient.init("localhost", 8881);
            nioClient.runSession();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
