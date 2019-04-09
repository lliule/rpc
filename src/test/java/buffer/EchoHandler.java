package buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author leliu
 */
public class EchoHandler implements Runnable {
	private SocketChannel socketChannel ;
	private ByteBuffer buffer = ByteBuffer.allocate(1024);
	public EchoHandler(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	@Override
	public void run() {
		try {
			while(-1 != socketChannel.read(buffer)) {
				buffer.flip();
				System.out.println(Charset.defaultCharset().newDecoder().decode(buffer).toString());
				socketChannel.write(ByteBuffer.wrap("i accept your message, 3Q!".getBytes()));
				if(buffer.hasRemaining()) {
					buffer.compact();
				} else {
					buffer.clear();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
