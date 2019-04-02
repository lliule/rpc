package serialize;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 序列化引擎：
 *  使序列化和反序列化方案可配置
 * @author leliu
 */
public class SerializerEngine {
	public static final Map<SerializeType, TSerialize> serializeMap = Maps.newConcurrentMap();

	static {
		serializeMap.put(SerializeType.DefaultJavaSerializer, new DefaultJavaSerialize());
		serializeMap.put(SerializeType.JsonSerializer, new JSONSerializer());
		serializeMap.put(SerializeType.HessianSerializer, new HessianSerializer());
	}

	public static <T> byte[] serialize (T obj, String serializeType) {
		SerializeType type = SerializeType.queryByType(serializeType);
		if(type == null) {
			throw new RuntimeException("serialize is null");
		}
		TSerialize serialize = serializeMap.get(type);
		if(serialize == null) {
			throw new RuntimeException("serialize error");
		}
		return serialize.serialize(obj);
	}

	public static <T> T deserialize(byte[] data, Class<T> clazz, String serializeType) {
		SerializeType type = SerializeType.queryByType(serializeType);
		if(type == null) {
			throw new RuntimeException("serialize is null");
		}
		TSerialize serialize = serializeMap.get(type);
		if(serialize == null) {
			throw new RuntimeException("serialize error");
		}
		return serialize.deserialize(data, clazz);
	}
}
