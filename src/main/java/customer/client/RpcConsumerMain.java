package customer.client;

import customer.framework.ConsumerProxy;
import customer.service.HelloService;

/**
 * RpcConsumerMain Create on 2019/4/2
 */
public class RpcConsumerMain {
	public static void main(String[] args) throws InterruptedException {
		HelloService service = ConsumerProxy.consume(HelloService.class, "127.0.0.1", 8083);
		for (int i = 0; i < 100; i++) {
			String hello = service.sayHello("dana_" + i);
			System.out.println(hello);
			Thread.sleep(1000);
		}
	}
}
