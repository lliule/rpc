package zookeeper;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.junit.Test;

/**
 * @author leliu
 */
public class ZookeeperClientDemo {
	@Test
	public void test() throws InterruptedException {
		String zkServer = "106.12.204.20:2181";
		int connectionTimeout = 3000;
		ZkClient zkClient = new ZkClient(zkServer, connectionTimeout);
		String path = "/zk-data";
		if(zkClient.exists(path)) {
			zkClient.delete(path);
		}

		// 创建持久节点
		zkClient.createPersistent(path);
		// 节点写入数据
		zkClient.writeData(path, "test_data_1");

		// 读取数据，第二个参数表明如果该节点不存在，将返回null
		String data = zkClient.readData(path, true);
		System.out.println(data);

		// 注册监听器，监听数据变化
		zkClient.subscribeDataChanges(path, new IZkDataListener() {
			@Override
			public void handleDataChange(String dataPath, Object obj) {
				System.out.println("handle data change, dataPath = " + dataPath + " data: " + obj);
			}

			@Override
			public void handleDataDeleted(String dataPath) {
				System.out.println("handle data deleted, dataPath: " + dataPath);
			}
		});
		// 修改数据
		zkClient.writeData(path, "test_data_2");
		Thread.sleep(1000);
		// 删除节点
		zkClient.delete(path);

		Thread.sleep(1000);
	}
}
