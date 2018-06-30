package cn.zkspy.manager.client;

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

public class ZookeeperClient {
	private static CuratorFramework client = null;

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
}
