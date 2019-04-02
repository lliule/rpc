package customer.framework;

import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 */
public class ProviderReflect {
	private static final ExecutorService executorService = Executors.newFixedThreadPool(5);

	public static void provider(final Object service, int port) throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);
		while (true) {
			final Socket socket = serverSocket.accept();
			executorService.execute( () -> {
				try {
					ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

					String methodName = inputStream.readUTF();
					System.out.println("service run : " + service.getClass().getName() + "#" + methodName);
					try {
						Object[] args = (Object[])inputStream.readObject();
						ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

						try {
							// 方法应用， 获取接口的返回值，并将结果写入输入流返回给客户端
							Object result = MethodUtils.invokeExactMethod(service, methodName, args);
							outputStream.writeObject(result);
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						} finally {
							outputStream.close();
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}finally {
						inputStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
}
