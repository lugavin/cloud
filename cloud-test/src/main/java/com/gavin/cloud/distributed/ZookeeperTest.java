package com.gavin.cloud.distributed;

import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.apache.zookeeper.CreateMode.EPHEMERAL;
import static org.apache.zookeeper.Watcher.Event.KeeperState;
import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class ZookeeperTest {

    private static final String CONNECTION_STRING = "127.0.0.1:2181";

    private static final int SESSION_TIMEOUT = 2000;

    public static void main(String[] args) throws Exception {
        ZooKeeper zooKeeper = null;
        try {
            CountDownLatch latch = new CountDownLatch(1);
            zooKeeper = new ZooKeeper(CONNECTION_STRING, SESSION_TIMEOUT, event -> {
                if (event.getState() == KeeperState.SyncConnected) {
                    switch (event.getType()) {
                        case None:
                            System.err.println("The client has connected to the server...");
                            latch.countDown();
                            break;
                        case NodeCreated:
                            System.err.println("====== Node Created ======");
                            break;
                        case NodeDeleted:
                            System.err.println("====== Node Deleted ======");
                            break;
                        case NodeDataChanged:
                            System.err.println("====== Node Data Changed ======");
                            break;
                        case NodeChildrenChanged:
                            System.err.println("====== Node Children Changed ======");
                            break;
                        default:
                            break;
                    }
                }
            });
            latch.await();
            String path = "/tmp";
            String data = "Test Data Node Value";
            // 注册 Watcher 方法
            zooKeeper.exists(path, true);
            zooKeeper.create(path, data.getBytes(), OPEN_ACL_UNSAFE, EPHEMERAL);
        } finally {
            if (zooKeeper != null) {
                TimeUnit.SECONDS.sleep(3);
                /**
                 * Once the client is closed, its session becomes invalid.
                 * All the ephemeral nodes in the ZooKeeper server associated with the session will be removed.
                 */
                zooKeeper.close();
            }
        }
    }

}
