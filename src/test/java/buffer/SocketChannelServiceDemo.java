package buffer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author leliu
 */
public class SocketChannelServiceDemo {
	private static ExecutorService executorService = Executors.newCachedThreadPool();

	public static void main(String[] args) throws IOException {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		if(serverSocketChannel.isOpen()) {
			// 设置为阻塞模式
			serverSocketChannel.configureBlocking(true);
			// 设置网络传输参数
			serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);

			serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);

			serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 8085));

			while (true) {
				// 等待客户端的请求
				SocketChannel accept = serverSocketChannel.accept();
				executorService.submit(new EchoHandler(accept));


			}
		}
	}
}
