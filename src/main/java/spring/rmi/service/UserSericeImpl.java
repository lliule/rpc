package spring.rmi.service;

import org.springframework.stereotype.Service;
import spring.model.User;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author leliu
 */
@Service("userService")
public class UserSericeImpl implements UserService{
	private static final Map<String, User> userMap = new HashMap<String, User>();
	static {
		userMap.put("dana", new User("dana", "dana@qq.com"));
		userMap.put("vic", new User("vic", "vic@qq.com"));
	}
	public User findByName(String userName) {
		return userMap.get(userName);
	}
}
