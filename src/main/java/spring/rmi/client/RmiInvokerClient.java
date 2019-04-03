package spring.rmi.client;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import spring.model.User;
import spring.rmi.service.UserService;

/**
 * @author leliu
 */
public class RmiInvokerClient {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.scan("classpath:spring.rmi.client");
		UserService userService = (UserService) applicationContext.getBean("userRmiServiceProxy");
		User dana = userService.findByName("dana");
		System.out.println(dana);
	}
}
