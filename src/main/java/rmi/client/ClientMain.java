package rmi.client;

import rmi.service.HelloService;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * 客户端
 */
public class ClientMain {
	public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
		HelloService helloService = (HelloService) Naming.lookup("rmi://localhost:8801/helloService");
		String dana = helloService.hello("dana");
		System.out.println("Rmi server return response : " + dana);
	}
}
