package buffer;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author leliu
 */
public class ChannelDemo {

	/**
	 * 使用channel transferTo 实现文件的读写
	 * @throws IOException
	 */
	@Test
	public void test1() throws IOException {
		FileInputStream fileInputStream = new FileInputStream(".gitignore");
		FileChannel channel = fileInputStream.getChannel();
		FileOutputStream outputStream = new FileOutputStream("target.txt");
		FileChannel outputStreamChannel = outputStream.getChannel();

		// 使用transferTo 将文件 .gitignore内容写入target.txt
		channel.transferTo(0, channel.size(), outputStreamChannel);
		channel.close();
		fileInputStream.close();
		outputStreamChannel.close();
		outputStream.close();
	}

	/**
	 * 使用channel read / writer 读写
	 * @throws IOException
	 */
	@Test
	public void test2() throws IOException {
		FileInputStream fileInputStream = new FileInputStream(".gitignore");
		FileChannel fileInputStreamChannel = fileInputStream.getChannel();
		FileOutputStream fileOutputStream = new FileOutputStream("target.txt");
		FileChannel outputStreamChannel = fileOutputStream.getChannel();

		ByteBuffer buffer = ByteBuffer.allocate(1024);
		int read = fileInputStreamChannel.read(buffer);

		while (read != -1 ) {
			buffer.flip();
			while (buffer.hasRemaining()) {
				outputStreamChannel.write(buffer);
			}
			buffer.clear();
			read = fileInputStreamChannel.read(buffer);
		}

		fileInputStream.close();
		fileInputStreamChannel.close();
		fileOutputStream.close();
		outputStreamChannel.close();

	}
}
