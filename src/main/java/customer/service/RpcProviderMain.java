package customer.service;

import customer.framework.ProviderReflect;

import java.io.IOException;

/**
 * RpcProviderMain Create on 2019/4/2
 */
public class RpcProviderMain {
	public static void main(String[] args) throws IOException {
		HelloServiceImpl helloService = new HelloServiceImpl();
		ProviderReflect.provider(helloService, 8083);
	}
}
