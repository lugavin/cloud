package com.gavin.cloud.distributed.registry;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ServiceConsumer {

    private static final String ZK_SERVERS = "127.0.0.1:2181";
    private static final String SERVICE_PATH = "/services/sys-service";

    private final ZkClient zkClient;
    private List<String> providers;

    private int reqCount;

    public ServiceConsumer() {
        this.zkClient = new ZkClient(ZK_SERVERS);
        this.providers = zkClient.getChildren(SERVICE_PATH).stream()
                .map(t -> (String) zkClient.readData(SERVICE_PATH + "/" + t))
                .collect(Collectors.toList());
        this.subscribeChildChanges(SERVICE_PATH);
    }

    public void send(String data) {
        String server = getServer();
        String[] split = server.split(":");
        try (Socket socket = new Socket(split[0], Integer.parseInt(split[1]))) {
            log.debug("Send data to the server: {}", server);
            // 向服务端发送数据
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            // 读取服务端数据
            DataInputStream in = new DataInputStream(socket.getInputStream());
            out.writeUTF(data);
            String ret = in.readUTF();
            log.debug("Receive server data: {}", ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void subscribeChildChanges(String path) {
        zkClient.subscribeChildChanges(path, (parentPath, currentChildNodes) ->
                this.providers = currentChildNodes.stream()
                        .map(t -> (String) zkClient.readData(path + "/" + t))
                        .collect(Collectors.toList())
        );
    }

    /**
     * 基于请求数实现本地负载均衡
     */
    private String getServer() {
        return providers.get(reqCount++ % providers.size());
    }

    public static void main(String[] args) throws IOException {
        ServiceConsumer consumer = new ServiceConsumer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        log.info("Type your message here to send...");
        String line;
        while ((line = reader.readLine()) != null) {
            consumer.send(line);
        }
    }

}
