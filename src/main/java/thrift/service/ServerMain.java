package thrift.service;

import com.facebook.nifty.core.NettyServerConfig;
import com.facebook.nifty.core.ThriftServerDef;
import com.facebook.swift.codec.ThriftCodecManager;
import com.facebook.swift.service.ThriftEventHandler;
import com.facebook.swift.service.ThriftServer;
import com.facebook.swift.service.ThriftServiceProcessor;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 */
public class ServerMain {
	public static void main(String[] args) {
		ThriftServiceProcessor processor = new ThriftServiceProcessor(new ThriftCodecManager(), new ArrayList<ThriftEventHandler>(), new HelloServiceImpl());
		ExecutorService executorService = Executors.newFixedThreadPool(1);
		ThriftServerDef build = ThriftServerDef.newBuilder().listen(8899).withProcessor(processor).using(executorService).build();

		ExecutorService bossExecutor = Executors.newCachedThreadPool();
		ExecutorService workerExecutor = Executors.newCachedThreadPool();
		NettyServerConfig serverConfig = NettyServerConfig.newBuilder().setBossThreadExecutor(bossExecutor).setWorkerThreadExecutor(workerExecutor).build();
		ThriftServer thriftServer = new ThriftServer(serverConfig, build);
		thriftServer.start();
	}
}
