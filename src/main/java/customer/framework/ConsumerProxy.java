package customer.framework;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Proxy;
import java.net.Socket;

/**
 */
public class ConsumerProxy {
	/**
	 * 服务消费代理
	 * @param interfaceClass interfaceClass
	 * @param host host
	 * @param port port
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T consume(final Class<T> interfaceClass, final String host, final int port) {
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
				new Class<?>[]{interfaceClass},
				((proxy, method, args) -> {
					Socket socket = new Socket(host, port);
					// 向服务端发送数据
					ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
					try {
						outputStream.writeUTF(method.getName());
						outputStream.writeObject(args);
						// 数据发送完，接受socket的返回值，并将结果返回
						ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
						try{
							Object result = inputStream.readObject();
							if(result instanceof Throwable) {
								throw (Throwable) result;
							}
							return result;
						}finally {
							inputStream.close();
						}
					} finally {
						outputStream.close();
						socket.close();
					}
				}));
	}
}
