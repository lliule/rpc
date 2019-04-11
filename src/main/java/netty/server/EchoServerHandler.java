package netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author leliu
 */
public class EchoServerHandler extends SimpleChannelInboundHandler {

	private static final String DELIMITER_TAG = "@#";

	// 计数器
	private static final AtomicInteger counter = new AtomicInteger(0);

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
//		detailWithout(channelHandlerContext, (ByteBuf) o);

		String content = (String) o;
		System.out.println("received from client: " + content + " counter: " + counter.addAndGet(1));

		// 加入分隔符，重新发送到客户端
		content += DELIMITER_TAG;
		ByteBuf echo = Unpooled.copiedBuffer(content.getBytes());
		channelHandlerContext.writeAndFlush(echo);
	}

	private void detailWithout(ChannelHandlerContext channelHandlerContext, ByteBuf o) throws UnsupportedEncodingException {
		// 接受客户端发来的数据，使用buf.readableBytes获取数据大小，并转化成Byte数组
		ByteBuf buf = o;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);

		// 将byte数组转成字符串，在控制台打印
		String body = new String(req, "UTF-8");
		System.out.println("receive data from client: " + body);
		// 将接收到的数据写回客户端
		ByteBuf resp = Unpooled.copiedBuffer(body.getBytes());
		channelHandlerContext.write(resp);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}

	/**
	 * 将发送缓冲区中的消息全部写入SocketChannel中
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
}
