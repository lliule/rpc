package thrift.client;

import com.facebook.nifty.client.FramedClientConnector;
import com.facebook.swift.service.ThriftClientManager;
import thrift.service.HelloService;
import thrift.service.User;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

/**
 */
public class Client {
	public static void main(String[] args) throws ExecutionException, InterruptedException {
		ThriftClientManager manager = new ThriftClientManager();
		FramedClientConnector connector = new FramedClientConnector(new InetSocketAddress("localhost", 8899));
		User user = new User();
		user.setEmail("test@163.com");
		user.setName("dana");
		HelloService helloService = manager.createClient(connector, HelloService.class).get();
		String hi = helloService.sayHello(user);
		System.out.println(hi);
	}
}
