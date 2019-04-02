package rmi.service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *  服务端方法必须继承UnicastRemoteObject类，定义了服务调用方法和服务提提供对象实例，并建立一对一的链接
 */
public class HelloServiceImpl extends UnicastRemoteObject implements HelloService {
	protected HelloServiceImpl() throws RemoteException {
	}

	public String hello(String someOne) throws RemoteException {
		return "hello, " + someOne;
	}
}
