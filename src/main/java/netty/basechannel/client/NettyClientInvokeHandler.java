package netty.basechannel.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty.basechannel.AresResponse;

/**
 * 客户端实现
 * @author leliu
 */
public class NettyClientInvokeHandler extends SimpleChannelInboundHandler<AresResponse> {
	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, AresResponse response) throws Exception {
		// 将netty异步返回的结果存入阻塞队列，以便调用端同步获取
		RevokeResponseHolder.putResultValue(response);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
