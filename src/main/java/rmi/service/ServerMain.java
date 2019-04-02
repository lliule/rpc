package rmi.service;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMISocketFactory;

/**
 * 服务端启动类
 */
public class ServerMain  {
	public static void main(String[] args) throws IOException, AlreadyBoundException {
		HelloServiceImpl helloService = new HelloServiceImpl();
		LocateRegistry.createRegistry(8801);
		// 注入自定义服务端通信端口，防止服务被防火墙拦截
		RMISocketFactory.setSocketFactory(new CustomerSocketFactory());
		Naming.bind("rmi://localhost:8801/helloService", helloService);
		System.out.println("ServerMain provide Rpc service now");
	}
}
