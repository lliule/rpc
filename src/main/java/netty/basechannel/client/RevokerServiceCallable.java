package netty.basechannel.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import netty.basechannel.AresRequest;
import netty.basechannel.AresResponse;
import netty.basechannel.util.NettyChannelPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * netty 请求发起调用线程
 * @author leliu
 */
public class RevokerServiceCallable implements Callable<AresResponse> {

	private static final Logger logger = LoggerFactory.getLogger(RevokerServiceCallable.class);

	private Channel  channel;

	private InetSocketAddress inetSocketAddress;

	private AresRequest request;

	public RevokerServiceCallable(InetSocketAddress inetSocketAddress, AresRequest request) {
		this.inetSocketAddress = inetSocketAddress;
		this.request = request;
	}

	public static RevokerServiceCallable of(InetSocketAddress inetSocketAddress, AresRequest request) {
		return new RevokerServiceCallable(inetSocketAddress, request);
	}


	@Override
	public AresResponse call() {
		// 初始化返回结果容器，将本次调用的唯一标识作为key存入返回结果的map

		RevokeResponseHolder.initResponseData(request.getUniqueKey());
		// 根据本地调用服务体重这地址获取netty通道channel队列
		ArrayBlockingQueue<Channel> blockingQueue = NettyChannelPoolFactory.channelPoolFactory().acquire(inetSocketAddress);


		try {
			if(channel == null ) {
				// 从队列中获取本次调用的netty通道channel
				channel = blockingQueue.poll(request.getInvokeTimeout(), TimeUnit.MILLISECONDS);
			}

			while(channel == null || !channel.isOpen() || !channel.isActive() || !channel.isWritable()) {
				logger.warn("-------retry get new Channel");
				channel = blockingQueue.poll(request.getInvokeTimeout(), TimeUnit.MILLISECONDS);
				if(channel == null) {
					channel = NettyChannelPoolFactory.channelPoolFactory().registerChannel(inetSocketAddress);
				}
			}

			// 将本次调用的信息写入netty通道，发起异步调用
			ChannelFuture channelFuture = channel.writeAndFlush(request);
			channelFuture.syncUninterruptibly();

			// 从返回结果容器中获取返回结果，同事设置等待超时时间为invokeTimeout
			long invokeTimeout = request.getInvokeTimeout();
			return RevokeResponseHolder.getValue(request.getUniqueKey(), invokeTimeout);
		} catch (InterruptedException e) {
			logger.error("service invoke error.", e);
		} finally {
			NettyChannelPoolFactory.channelPoolFactory().release(blockingQueue, channel, inetSocketAddress);
		}

		return null;
	}
}
