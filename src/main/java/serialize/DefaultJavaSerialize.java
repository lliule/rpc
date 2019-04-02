package serialize;

import java.io.*;

/**
 * DefaultJavaSerialize Create on 2019/4/2
 * java 默认的序列化和反序列化实现
 * 优点：
 *      java自带，无需引入第三方依赖
 *      与java语言有天然的最好的易用性和亲和性
 *  缺点：
 *      只支持java
 *      性能欠佳，序列化后的产生的码流过大，对于引用过深的对象序列化易发生内存OOM异常
 */
public class DefaultJavaSerialize implements TSerialize{
	@Override
	public <T> byte[] serialize(T obj) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(obj);
			objectOutputStream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return byteArrayOutputStream.toByteArray();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T deserialize(byte[] data, Class<T> clazz) {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			return  (T)objectInputStream.readObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
