package async.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * 哭护短业务逻辑
 * @author leliu
 */
public class AsyncEchoClientHandler implements CompletionHandler<Void, AsyncEchoClientHandler>, Runnable {

	private AsynchronousSocketChannel client;
	private String host;
	private int port;
	private CountDownLatch latch;

	public AsyncEchoClientHandler(String host, int port) {
		this.host = host;
		this.port = port;
		try {
			client = AsynchronousSocketChannel.open();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		latch = new CountDownLatch(1);
		client.connect(new InetSocketAddress(host, port), this, this);
		try {
			latch.await();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void completed(Void result, AsyncEchoClientHandler attachment) {
		byte[] req = "你好, java Asynchronous IO.".getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
		writeBuffer.put(req);
		writeBuffer.flip();
		// 将数据写入服务端
		client.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
			@Override
			public void completed(Integer result, ByteBuffer attachment) {
				if(attachment.hasRemaining()) {
					client.write(attachment, attachment, this);
				} else {
					ByteBuffer readBuffer = ByteBuffer.allocate(1024);
					// 读取从服务端传回的数据
					client.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>() {
						@Override
						public void completed(Integer result, ByteBuffer attachment) {
							attachment.flip();
							byte[] bytes = new byte[attachment.remaining()];
							attachment.get(bytes);
							String  body;

							try {
								body = new String(bytes, "UTF-8");
								System.out.println("client echo content is :" + body);
								latch.countDown();
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
						}
						// 服务端数据返回出错
						@Override
						public void failed(Throwable exc, ByteBuffer attachment) {
							try {
								client.close();
								latch.countDown();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
				}
			}

			// 客户端数据写入服务端写入出错
			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					latch.countDown();
				}
			}
		});
	}

	@Override
	public void failed(Throwable exc, AsyncEchoClientHandler attachment) {
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			latch.countDown();
		}
	}
}
