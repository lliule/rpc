package async.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * 服务端实现代码
 * @author leliu
 */
public class AsyncEchoServerHandler implements Runnable{

	private int port;
	CountDownLatch latch;

	AsynchronousServerSocketChannel asynchronousServerSocketChannel;

	public AsyncEchoServerHandler(int port) {
		this.port = port;
		try {
			asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
			asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		latch = new CountDownLatch(1);
		doAccept();

		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 授权客户端连接
	 */
	private void doAccept() {
		asynchronousServerSocketChannel.accept(this, new AccepCompletionHandler());
	}

	private class AccepCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncEchoServerHandler> {

		@Override
		public void completed(AsynchronousSocketChannel result, AsyncEchoServerHandler attachment) {
			// 循环介入客户端
			attachment.asynchronousServerSocketChannel.accept(attachment,this);
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			result.read(buffer, buffer, new RaadCompletionHandler(result));

		}

		@Override
		public void failed(Throwable exc, AsyncEchoServerHandler attachment) {
			exc.printStackTrace();
			attachment.latch.countDown();
		}
	}
}
