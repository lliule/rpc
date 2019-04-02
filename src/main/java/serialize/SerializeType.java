package serialize;

import org.apache.commons.lang3.StringUtils;

/**
 * @author leliu
 */
public enum SerializeType {
	/**
	 * 默认的java序列化方式
	 */
	DefaultJavaSerializer("DefaultJavaSerializer"),
	/**
	 * json序列化方式
	 */
	JsonSerializer("JsonSerilizer"),
	/**
	 * hessian序列化方式
	 */
	HessianSerializer("HessianSerializer");


	private String serializeType;

	SerializeType(String serializeType) {
		this.serializeType = serializeType;
	}

	public static SerializeType queryByType(String serializeType) {
		if(StringUtils.isBlank(serializeType)) {
			return null;
		}
		for (SerializeType serialize : SerializeType.values()) {
			if(StringUtils.equals(serializeType, serialize.serializeType)) {
				return serialize;
			}
		}
		return null;
	}
}
