package netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 *
 * @author leliu
 */
public class EchoServer {

	//使用特殊符号作为包的结束符
	private static final String DELIMITER_TAG = "@#";

	public static void main(String[] args) {
		int port = 8080;

		new EchoServer().bind(port);
	}

	private void bind(int port) {
		// 创建两个EventLoopGroup实例
		// EventLoopGroup是包含一组专门用于处理网络事件的nio线程组

		NioEventLoopGroup bossGroup = new NioEventLoopGroup();
		NioEventLoopGroup workerGroup = new NioEventLoopGroup();

		// 创建服务端辅助启动类
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class) // 设置NioServerSocketChannel，对应jdk的ServerSocketChannel
		.option(ChannelOption.SO_BACKLOG, 1024) // 设置TCP参数，连接请求的最大队列长度
		.childHandler(new ChannelInitializer<NioSocketChannel>() {
			@Override
			protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
				ByteBuf delimiter = Unpooled.copiedBuffer(DELIMITER_TAG.getBytes());
				nioSocketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
				nioSocketChannel.pipeline().addLast(new StringDecoder());
				nioSocketChannel.pipeline().addLast(new EchoServerHandler());
			}
		});

		try {
			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// 优雅退出，释放线程资源
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}

	}
}
