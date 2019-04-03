package spring.rmi.service;


import spring.model.User;

public interface UserService {
	User findByName(String userName);
}
