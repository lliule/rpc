package async.client;

/**
 * 客户端启动代码
 * @author leliu
 */
public class AIOEchoClient {
	public static void main(String[] args) {
		int port = 8080;
		new Thread(new AsyncEchoClientHandler("localhost", port)).start();
	}

}
