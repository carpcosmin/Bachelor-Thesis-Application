package com.aplicatielicenta.springserver.entities.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDao {

    @Autowired
    UserRepository userRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        Streamable.of(userRepository.findAll())
                .forEach(users::add);
        return users;
    }

    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }

}
