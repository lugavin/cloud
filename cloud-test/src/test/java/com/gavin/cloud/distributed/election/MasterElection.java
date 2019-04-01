package com.gavin.cloud.distributed.election;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

@Slf4j
@Component
public class MasterElection implements ApplicationRunner {

    private static final String ZK_ADDRESS = "127.0.0.1:2181";
    private static final String ZK_ROOT = "/election";

    private final ServerProperties serverProperties;
    private final ZkClient zkClient;

    public MasterElection(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
        this.zkClient = new ZkClient(ZK_ADDRESS);
    }

    @Override
    public void run(ApplicationArguments args) {
        if (electMaster()) {
            log.info("====== 选举为Master ======");
        }
        zkClient.subscribeDataChanges(ZK_ROOT, new IZkDataListener() {
            @Override
            public void handleDataDeleted(String dataPath) {
                log.debug("====== 重新选举Master ======");
                electMaster();
            }

            @Override
            public void handleDataChange(String dataPath, Object data) {

            }
        });
    }

    private boolean electMaster() {
        try {
            zkClient.createEphemeral(ZK_ROOT, getServer());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getServer() throws UnknownHostException {
        InetAddress inetAddress = Optional.ofNullable(serverProperties.getAddress())
                .orElse(InetAddress.getByName("127.0.0.1"));
        return inetAddress.getHostAddress() + ":" + serverProperties.getPort();
    }

}
