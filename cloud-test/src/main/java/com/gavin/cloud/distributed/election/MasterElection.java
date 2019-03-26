package com.gavin.cloud.distributed.election;

import com.gavin.cloud.config.properties.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Slf4j
@Component
public class MasterElection implements ApplicationRunner {

    private final ServerProperties serverProperties;
    private final AppProperties.Zookeeper zkProperties;
    private final ZkClient zkClient;

    public MasterElection(ServerProperties serverProperties, AppProperties appProperties) {
        this.serverProperties = serverProperties;
        this.zkProperties = appProperties.getZookeeper();
        this.zkClient = new ZkClient(zkProperties.getAddress());
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (electMaster()) {
            log.info("====== 选举为Master ======");
        }
        zkClient.subscribeDataChanges(zkProperties.getRoot(), new IZkDataListener() {
            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                log.debug("====== 重新选举Master ======");
                electMaster();
            }

            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {

            }
        });
    }

    private boolean electMaster() {
        try {
            zkClient.createEphemeral(zkProperties.getRoot(), getServer());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getServer() {
        InetAddress address = serverProperties.getAddress();
        return (address != null ? address.getHostAddress() : "127.0.0.1") + ":" + serverProperties.getPort();
    }

}
