package async.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 服务端业务逻辑实现
 * @author leliu
 */
public class RaadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
	private AsynchronousSocketChannel channel;

	public RaadCompletionHandler(AsynchronousSocketChannel channel) {
		if(this.channel == null) {
			this.channel = channel;
		}
	}


	@Override
	public void completed(Integer result, ByteBuffer attachment) {
		// 获取客户端传入的数据
		attachment.flip();
		byte[] body = new byte[attachment.remaining()];

		attachment.get(body);

		try {
			String req = new String(body, "UTF-8");
			System.out.println("echo content: " + req);
			doWrite(req);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	private void doWrite(String req) {
		if(req != null && req.trim().length() > 0) {
			byte[] bytes = req.getBytes();
			final ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);

			writeBuffer.put(bytes);
			writeBuffer.flip();


			channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
				@Override
				public void completed(Integer result, ByteBuffer attachment) {
					// 如果没有发送完成，继续发送
					if(attachment.hasRemaining()) {
						channel.write(attachment, attachment, this);
					}
				}

				@Override
				public void failed(Throwable exc, ByteBuffer attachment) {

				}
			});
		}
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		try {
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
