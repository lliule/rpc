package buffer;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 *
 * @author leliu
 */
public class SocketChannelClientDemo  {
	public static void main(String[] args) throws IOException {
		ByteBuffer helloBuffer = ByteBuffer.wrap("你 好， java blocking I/O !".getBytes());
		Charset charset = Charset.defaultCharset();
		CharsetDecoder decoder = charset.newDecoder();
		CharBuffer charBuffer;
		SocketChannel socketChannel = SocketChannel.open();
		if(socketChannel.isOpen()) {
			socketChannel.configureBlocking(true);
			socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 128 * 1024);
			socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 128 * 1024);
			socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
			socketChannel.setOption(StandardSocketOptions.SO_LINGER , 5);

			socketChannel.connect(new InetSocketAddress("127.0.0.1", 8085));

			if(socketChannel.isConnected()) {
				socketChannel.write(helloBuffer);
				ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
				while (socketChannel.read(byteBuffer) != -1) {
					byteBuffer.flip();
					charBuffer = decoder.decode(byteBuffer);
					System.out.println(charBuffer.toString());

					if(byteBuffer.hasRemaining()) {
						byteBuffer.compact();
					}else {
						byteBuffer.clear();
					}
				}
				socketChannel.close();
			}
		}



	}
}
