package cn.zkspy.curator.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

public class ZookeeperClient {
    private static CuratorFramework client = null;

    @SuppressWarnings("unlikely-arg-type")
    public static boolean reBuildClient(String connectString) throws Exception {
        client = CuratorFrameworkFactory.newClient(connectString, new RetryUntilElapsed(1000 * 5, 1000));

        final CountDownLatch lock = new CountDownLatch(1);

        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                if (ConnectionState.CONNECTED.equals(newState)) {
                    lock.countDown();
                }
            }

        });
        client.start();
        lock.await(5, TimeUnit.SECONDS);
        if (!ZooKeeper.States.CONNECTED.equals(client.getZookeeperClient().getZooKeeper().getState())) {
            return false;
        }
        return true;
    }

    public static CuratorFramework getClient() {
        return client;
    }

    public static void closeClient() {
        if (null != client && CuratorFrameworkState.STARTED.equals(client.getState())) {
            client.close();
        }
    }

    public static NodeData getAllData(String path) throws Exception {

        if (null == ZookeeperClient.client || CuratorFrameworkState.STOPPED.equals(ZookeeperClient.client.getState())) {
            return new NodeData();
        }

        NodeData curNodeData = new NodeData();
        curNodeData.setCurPath(path);

        // Data
        byte[] dataBytes = ZookeeperClient.client.getData().forPath(path);
        if (null != dataBytes) {
            String curData = new String(dataBytes);
            curNodeData.setData(curData);
        }

        // ACL
        List<Map<String, String>> returnACLs = new ArrayList<Map<String, String>>();
        List<ACL> aclList = ZookeeperClient.client.getACL().forPath(path);
        for (ACL acl : aclList) {
            Map<String, String> aclMap = new LinkedHashMap<String, String>();
            aclMap.put(Constant.ACLDataKey.ACL_SCHEME, acl.getId().getScheme());
            aclMap.put(Constant.ACLDataKey.ACL_ID, acl.getId().getId());
            StringBuilder sb = new StringBuilder();
            int perms = acl.getPerms();
            boolean addedPerm = false;
            if ((perms & Perms.READ) == Perms.READ) {
                sb.append("Read");
                addedPerm = true;
            }
            if (addedPerm) {
                sb.append(", ");
            }
            if ((perms & Perms.WRITE) == Perms.WRITE) {
                sb.append("Write");
                addedPerm = true;
            }
            if (addedPerm) {
                sb.append(", ");
            }
            if ((perms & Perms.CREATE) == Perms.CREATE) {
                sb.append("Create");
                addedPerm = true;
            }
            if (addedPerm) {
                sb.append(", ");
            }
            if ((perms & Perms.DELETE) == Perms.DELETE) {
                sb.append("Delete");
                addedPerm = true;
            }
            if (addedPerm) {
                sb.append(", ");
            }
            if ((perms & Perms.ADMIN) == Perms.ADMIN) {
                sb.append("Admin");
                addedPerm = true;
            }
            aclMap.put(Constant.ACLDataKey.ACL_PERMS, sb.toString());
            returnACLs.add(aclMap);
        }
        curNodeData.setAclData(returnACLs);

        // MetaData
        Map<String, String> metaData = new HashMap<String, String>();
        Stat stat = ZookeeperClient.client.checkExists().forPath(path);

        metaData.put(Constant.MetaDataKey.A_VERSION, String.valueOf(stat.getAversion()));
        metaData.put(Constant.MetaDataKey.C_TIME, String.valueOf(stat.getCtime()));
        metaData.put(Constant.MetaDataKey.C_VERSION, String.valueOf(stat.getCversion()));
        metaData.put(Constant.MetaDataKey.CZXID, String.valueOf(stat.getCzxid()));
        metaData.put(Constant.MetaDataKey.DATA_LENGTH, String.valueOf(stat.getDataLength()));
        metaData.put(Constant.MetaDataKey.EPHEMERAL_OWNER, String.valueOf(stat.getEphemeralOwner()));
        metaData.put(Constant.MetaDataKey.M_TIME, String.valueOf(stat.getMtime()));
        metaData.put(Constant.MetaDataKey.MZXID, String.valueOf(stat.getMzxid()));
        metaData.put(Constant.MetaDataKey.NUM_CHILDREN, String.valueOf(stat.getNumChildren()));
        metaData.put(Constant.MetaDataKey.PZXID, String.valueOf(stat.getPzxid()));
        metaData.put(Constant.MetaDataKey.VERSION, String.valueOf(stat.getVersion()));

        curNodeData.setMetaData(metaData);

        return curNodeData;
    }
}
