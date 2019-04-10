package async.server;

/**
 * 服务端启动
 * @author leliu
 */
public class AIOEchoServer {

	public static void main(String[] args) {
		int port = 8080;

		AsyncEchoServerHandler handler = new AsyncEchoServerHandler(port);
		new Thread(handler).start();
	}
}
