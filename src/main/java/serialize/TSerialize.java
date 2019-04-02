package serialize;

public interface TSerialize {

	/**
	 * 序列化对象
	 * @param obj obj
	 * @param <T> 类型
	 * @return byte[]
	 */
	<T> byte[] serialize(T obj);

	/**
	 * 反序列化
	 * @param data byte数据
	 * @param clazz 类型
	 * @param <T> T
	 * @return T
	 */
	<T> T deserialize(byte[] data, Class<T> clazz);

}
