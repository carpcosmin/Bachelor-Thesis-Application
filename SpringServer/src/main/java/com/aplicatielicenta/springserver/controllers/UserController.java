package com.aplicatielicenta.springserver.controllers;

import com.aplicatielicenta.springserver.entities.user.LogInRequest;
import com.aplicatielicenta.springserver.entities.user.User;
import com.aplicatielicenta.springserver.entities.user.UserDao;
import com.aplicatielicenta.springserver.entities.user.UserRepository;
import com.aplicatielicenta.springserver.services.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static javax.crypto.Cipher.SECRET_KEY;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/user/get-all")
    public List<User> getAllUsers(){
        return userDao.getAllUsers();
    }

    @GetMapping("/user/{uid}")
    public ResponseEntity<User> getUserById(@PathVariable Long uid) {
        User user = userRepository.findById(uid);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/user/token/{token}")
    public User getUserByToken(@PathVariable String token) {
        User user = userRepository.findByToken(token);
        if (user == null) {

        }
        return user;
    }

    @PostMapping("/user/save")
    public User saveUser (@RequestBody User user){
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userDao.saveUser(user);
    }

    @PostMapping("/user/login")
    public User login(@RequestBody LogInRequest loginRequest) {
        User user = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        if (user != null) {
            
        } else {
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return user;
    }

    @PutMapping("/user/{uid}")
    public ResponseEntity<User> updateUserById(@PathVariable Long uid, @RequestBody User user) {
        User existingUser = userRepository.findById(uid);
        if(existingUser != null){
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhoneNumber(user.getPhoneNumber());

            // check if the password needs to be updated
            if (!existingUser.getPassword().equals(user.getPassword())) {
                // hash the new password
                String hashedPassword = passwordEncoder.encode(user.getPassword());
                existingUser.setPassword(hashedPassword);
            }

            User updatedUser = userRepository.save(existingUser);
            return ResponseEntity.ok(updatedUser);
        }
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/user/{uid}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long uid) {
        User existingUser = userRepository.findById(uid);
        if (existingUser != null) {
            userRepository.deleteById(existingUser.getId());
            return ResponseEntity.ok().build();
        }
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
