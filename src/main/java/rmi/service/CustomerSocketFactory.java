package rmi.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

/**
 * 自定义通信端口，防止被防火墙拦截
 */
public class CustomerSocketFactory extends RMISocketFactory {
	@Override
	public Socket createSocket(String host, int port) throws IOException {
		return new Socket(host, port);
	}

	@Override
	public ServerSocket createServerSocket(int port) throws IOException {
		if(port == 0) {
			port = 8501;
		}
		System.out.println("rmi nodify port:" + port);
		return new ServerSocket(port);
	}
}
