package spring.rmi.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import spring.rmi.service.UserService;

/**
 * RmiRpcClientConfig Create on 2019/4/3
 *
 * @author leliu
 */
@Configuration
public class RmiRpcClientConfig {

	@Bean
	public RmiProxyFactoryBean rmiProxyFactoryBean () {
		RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
		rmiProxyFactoryBean.setServiceUrl("rmi://127.0.0.1:1199/userRmiService");
		rmiProxyFactoryBean.setServiceInterface(UserService.class);
		return rmiProxyFactoryBean;
	}

}
