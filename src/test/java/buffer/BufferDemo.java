package buffer;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * @author leliu
 */
public class BufferDemo {

	@Test
	public void test() {
		ByteBuffer buffer = ByteBuffer.allocate(10);
		System.out.println("Capacity :" + buffer.capacity());
		System.out.println("limit:" + buffer.limit());
		System.out.println("position: " + buffer.position());
		System.out.println("Remaining: " + buffer.remaining());
		System.out.println("设置buffer的limit 属性为6");
		buffer.limit(6);
		System.out.println("limit:" + buffer.limit());
		System.out.println("position: " + buffer.position());
		System.out.println("Remaining: " + buffer.remaining());

		buffer.position(2);
		System.out.println("position: " + buffer.position());
		System.out.println("Remaining: " + buffer.remaining());

		System.out.println(buffer);
	}

	@Test
	public void test2() {
		String content = "你好!java Non-Blocking I/O.";
		CharBuffer buff = CharBuffer.allocate(50);
		for (int i = 0; i < content.length(); i++) {
			buff.append(content.charAt(i));
		}
		// 反转buffer，准备读取buffer内容
		buff.flip();

		while (buff.hasRemaining()) {
			System.out.print(buff.get());
		}

		// 倒回读取之前，准备再次读取
		buff.rewind();
		System.out.println();

		while (buff.hasRemaining()) {
			System.out.print(buff.get());
		}
		System.out.println();

		buff.clear();
		buff.put('你').put('好').put('!');
		buff.flip();

		while (buff.hasRemaining()) {
			System.out.print(buff.get());
		}

	}


	@Test
	public void test3(){
		System.out.println("-----");
		Thread.currentThread().interrupt();
		System.out.println("++++++++");
	}
}
