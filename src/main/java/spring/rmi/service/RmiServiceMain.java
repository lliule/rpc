package spring.rmi.service;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author leliu
 */
public class RmiServiceMain {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.scan("classpath:spring.rmi.service");
	}
}
