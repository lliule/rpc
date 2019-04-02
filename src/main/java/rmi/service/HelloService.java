package rmi.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 */
public interface HelloService  extends Remote {
	String hello (String someOne) throws RemoteException;
}
