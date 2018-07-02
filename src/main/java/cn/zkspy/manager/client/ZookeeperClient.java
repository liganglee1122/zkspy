package cn.zkspy.manager.client;

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
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import cn.zkspy.gui.NodeData;
import cn.zkspy.gui.ZkspyNodeViewerPanel;

public class ZookeeperClient {
	private static CuratorFramework client = null;
	
	private static final String ACL_PERMS = "Permissions";
	private static final String ACL_SCHEME = "Scheme";
	private static final String ACL_ID = "Id";

	@SuppressWarnings("unlikely-arg-type")
	public static boolean reBuildClient(String connectString) throws Exception {
		//
		ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, Integer.MAX_VALUE);
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
	
	public static NodeData getAllData(String path) throws Exception
	{
		byte[] bytes = ZookeeperClient.getClient().getData().forPath(path);
		NodeData curNodeData = new NodeData();
		curNodeData.setCurPath(path);
		if (null != bytes) {
			String curData = new String(bytes);
			curNodeData.setData(curData);
		}

		// ACL
		List<Map<String, String>> returnACLs = new ArrayList<Map<String, String>>();

		List<ACL> aclList = ZookeeperClient.getClient().getACL().forPath(path);
		for (ACL acl : aclList) {
			Map<String, String> aclMap = new LinkedHashMap<String, String>();
			aclMap.put(ACL_SCHEME, acl.getId().getScheme());
			aclMap.put(ACL_ID, acl.getId().getId());
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
			aclMap.put(ACL_PERMS, sb.toString());
			returnACLs.add(aclMap);
		}
		curNodeData.setAclData(returnACLs);
		Map<String, String> metaData = new HashMap<String, String>();
		// MetaData
		Stat stat = ZookeeperClient.getClient().checkExists()
				.forPath(path);
		metaData.put("ACL Version,", String.valueOf(stat.getAversion()));

		metaData.put("CreationTime", String.valueOf(stat.getCtime()));

		metaData.put("Children Version", String.valueOf(stat.getCversion()));

		metaData.put("CreationID", String.valueOf(stat.getCzxid()));

		curNodeData.setMetaData(metaData);
		return curNodeData;
	}
}
