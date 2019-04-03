package spring.rmi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiServiceExporter;

/**
 * @author leliu
 */
@Configuration
public class RmiRpcServiceConfig {
	@Autowired
	private UserService userService;
	@Bean
	public RmiServiceExporter rmiServiceExporter (){
		RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
		rmiServiceExporter.setServiceName("userRmiService");
		rmiServiceExporter.setService(userService);
		rmiServiceExporter.setServiceInterface(UserService.class);
		rmiServiceExporter.setRegistryPort(1199);
		return rmiServiceExporter;
	}
}
