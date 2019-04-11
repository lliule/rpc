package netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 处理客户端I/O
 * @author leliu
 */
public class EchoClientHandler extends SimpleChannelInboundHandler {
	private static final Logger logger = LoggerFactory.getLogger(EchoClientHandler.class);

	private static final AtomicInteger counter = new AtomicInteger(0);

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
//		detailWithout((ByteBuf) o);
		String content = (String) o;
		System.out.println("receive from server: " + content + " counter:" + counter.addAndGet(1));

	}

	private void detailWithout(ByteBuf o) throws UnsupportedEncodingException {
		ByteBuf buf = o;

		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);

		String body = new String(req, "UTF-8");
		System.out.println("receive data from server: " + body);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
}
