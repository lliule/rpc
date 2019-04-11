package netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author leliu
 */
public class EchoClient {

	private static final String DELIMITER_TAG = "@#";
	public static void main(String[] args) {
		int port = 8080;

		new EchoClient().connect(port, "localhost");
	}

	private void connect(int port, String host) {
		//创建客户端处理I/O读写的NIO线程组
		NioEventLoopGroup group = new NioEventLoopGroup();

		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group)
				.channel(NioSocketChannel.class)
				// 设置TCP参数
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<NioSocketChannel>() {
					@Override
					protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
						ByteBuf delimiter = Unpooled.copiedBuffer(DELIMITER_TAG.getBytes());
						nioSocketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));

						nioSocketChannel.pipeline().addLast(new StringDecoder());

						nioSocketChannel.pipeline().addLast(new EchoClientHandler());
					}
				});

		try {
			ChannelFuture f = bootstrap.connect(host, port).sync();
			// for 之后会发生黏包、半包现象
			for (int i = 0; i < 100; i++) {
				byte[] req = ("你好，Netty！" + DELIMITER_TAG).getBytes();
				ByteBuf messageBuffer = Unpooled.buffer(req.length);
				messageBuffer.writeBytes(req);

				// 向服务端发送数据
				ChannelFuture future = f.channel().writeAndFlush(messageBuffer);
				future.syncUninterruptibly();
			}

			// 等待客户端链路关闭
			f.channel().closeFuture().sync();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}

	}
}
