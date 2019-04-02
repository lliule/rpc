package serialize;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * HessianSerializer Create on 2019/4/2
 * 支持跨语言的序列化/反序列化 hessian
 * @author leliu
 */
public class HessianSerializer implements TSerialize{
	public <T> byte[] serialize(T obj) {
		if(obj == null) {
			return new byte[0];
		}

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		HessianOutput hessianOutput = new HessianOutput(byteArrayOutputStream);
		try {
			hessianOutput.writeObject(obj);
			return byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T deserialize(byte[] data, Class<T> clazz) {
		if(data == null) {
			throw new NullPointerException();
		}

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
		HessianInput hessianInput = new HessianInput(byteArrayInputStream);
		try {
			return (T) hessianInput.readObject(clazz);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
