package netty.basechannel.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import netty.basechannel.AresRequest;
import netty.basechannel.NettyDecoderHander;
import netty.basechannel.NettyEncoderHandler;
import register.help.PropertyConfigeHelper;
import serialize.SerializeType;

/**
 *
 * @author leliu
 */
public class NettyServer {
	private NettyServer () {}

	private static NettyServer nettyServer = new NettyServer();

	/**
	 服务端boss线程组
	 */
	private EventLoopGroup bossGroup;

	/**
	 * 服务端worker线程组
	 */
	private EventLoopGroup workerGroup;

	private SerializeType serializeType = PropertyConfigeHelper.getSerializeType();

	public void start(final int port) {
		synchronized (NettyServer.class) {
			if(bossGroup != null || workerGroup != null) {
				return;
			}

			bossGroup = new NioEventLoopGroup();
			workerGroup = new NioEventLoopGroup();

			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 1024)
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					.childOption(ChannelOption.TCP_NODELAY, true)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel socketChannel) throws Exception {
							// 注册解码器
							socketChannel.pipeline().addLast(new NettyDecoderHander(AresRequest.class, serializeType));
							// 注册编码器
							socketChannel.pipeline().addLast(new NettyEncoderHandler(serializeType));
							// 注册服务端业务逻辑处理器
							socketChannel.pipeline().addLast(new NettyServerInvokeHandler());
						}
					});

			try {
				serverBootstrap.bind(port).sync().channel();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static NettyServer singleton() {
		return nettyServer;
	}

}
