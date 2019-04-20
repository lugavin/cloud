package com.gavin.cloud.zookeeper.registry;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ServiceProvider {

    private final ExecutorService threadPool = Executors.newFixedThreadPool(15);

    private static final String ZK_SERVERS = "127.0.0.1:2181";
    private static final String ROOT_PATH = "/services";
    private static final String SERVICE_NAME = "sys-service";

    public void listen(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        register(serverSocket);
        while (true) {
            Socket socket = serverSocket.accept(); // 阻塞
            threadPool.execute(() -> {
                try {
                    // 读取客户端数据
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    // 向客户端发送数据
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    String line = in.readUTF(); // 阻塞
                    out.writeUTF(line);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeQuietly(socket);
                }
            });
        }
    }

    /**
     * 注册服务
     */
    private void register(ServerSocket serverSocket) {
        ZkClient zkClient = new ZkClient(ZK_SERVERS);
        if (!zkClient.exists(ROOT_PATH)) {  // 如果根节点不存在则创建根节点
            zkClient.createPersistent(ROOT_PATH);
        }
        String servicePath = ROOT_PATH + "/" + SERVICE_NAME;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath);
        }
        String path = servicePath + "/" + SERVICE_NAME + "_" + serverSocket.getLocalPort();
        if (zkClient.exists(path)) {
            zkClient.delete(path);
        }
        // 创建临时节点
        zkClient.createEphemeral(path, serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort());
    }

    private void closeQuietly(final Socket sock) {
        if (sock != null) {
            try {
                sock.close();
            } catch (IOException e) {
                // ignored
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new ServiceProvider().listen(8080);
    }

}