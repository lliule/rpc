package netty.basechannel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import serialize.SerializeType;
import serialize.SerializerEngine;

/**
 * 自定义编码器，负责将java对象序列化为字节数组
 * @author leliu
 */
public class NettyEncoderHandler extends MessageToByteEncoder {

	private SerializeType serializeType;

	public NettyEncoderHandler(SerializeType serializeType) {
		this.serializeType = serializeType;
	}

	@Override
	protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
		//序列化成字节数组
		byte[] data = SerializerEngine.serialize(o, serializeType.getSerializeType());
		// 将字节数字的长度作为消息头写入，解决半包/黏包问题
		byteBuf.writeInt(data.length);

		byteBuf.writeBytes(data);
	}
}
