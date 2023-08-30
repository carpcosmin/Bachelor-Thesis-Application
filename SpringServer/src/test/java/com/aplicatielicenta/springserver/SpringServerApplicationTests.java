package com.aplicatielicenta.springserver;

import com.aplicatielicenta.springserver.entities.user.User;
import com.aplicatielicenta.springserver.entities.user.UserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SpringServerApplicationTests {
	@Autowired
	 private UserDao userDao;

//	@Test
	void addUserTest() {
		User user = new User();
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setEmail("user@gmail.com");
		user.setPassword("user123");
		userDao.saveUser(user);
	}

//	@Test
	void getAllUsers(){
		List<User> users= userDao.getAllUsers();
		System.out.println(users);
	}

//	@Test
	void getAllUsersAndDelete(){
		List<User> users= userDao.getAllUsers();
		for (User user : users) {
			userDao.deleteUser(user.getId());
		}
	}
}
