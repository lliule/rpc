package netty.basechannel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import serialize.SerializeType;
import serialize.SerializerEngine;

import java.util.List;

/**
 * 自定义解码器 ，负责将字节数组解码为java对象
 * @author leliu
 */
public class NettyDecoderHander extends ByteToMessageDecoder {
	private Class<?> genericClass;

	private SerializeType serializeType;

	public NettyDecoderHander(Class<?> genericClass, SerializeType serializeType) {
		this.genericClass = genericClass;
		this.serializeType = serializeType;
	}

	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
		if(byteBuf.readableBytes() < 4) {
			return ;
		}

		byteBuf.markReaderIndex();
		int dataLength = byteBuf.readInt();

		if(dataLength < 0) {
			channelHandlerContext.close();
		}

		// 若当前可以获取到的字节数小于实际长度，则直接返回，知道当前可以获取到的字节数等于实际长度
		if (byteBuf.readableBytes() < dataLength) {
			byteBuf.resetReaderIndex();
			return;
		}

		byte[] data = new byte[dataLength];
		byteBuf.readBytes(data);

		Object obj = SerializerEngine.deserialize(data, genericClass, serializeType.getSerializeType());
		list.add(obj);

	}
}
