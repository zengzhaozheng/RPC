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
        //ΪsocketServer�󶨶˿�
        this.serverSocketChannel.socket().bind(new InetSocketAddress(this.port));
        //��selector,���ҽ��ڷ����channl��ע���selector������¼�
        this.selector = Selector.open();
        //ע����Խ��н��տͻ��������¼�
        this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server Start at " + this.port);
    }

    //����,���selector���¼��ı仯��������
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

    //��������
    private void process(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = null;
        SocketChannel client = null;
        String receiveText;
        String sendText;
        int count = 0;
        //������Ƿ�׼���ÿͻ������ӽ���
        if (key.isValid() && key.isAcceptable()) {
            serverSocketChannel = (ServerSocketChannel) key.channel();
            client = serverSocketChannel.accept();
            client.configureBlocking(false);
            System.out.println("client has conneted !");
            //���ͻ���ע�ᵽselector
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
            //������������Ϊ�ɿɶ�״̬
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
