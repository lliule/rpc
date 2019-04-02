package thrift.service;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;

/**
 */
@ThriftService("helloService")
public interface HelloService {
	@ThriftMethod
	String sayHello(@ThriftField(name = "user") User user);
}
