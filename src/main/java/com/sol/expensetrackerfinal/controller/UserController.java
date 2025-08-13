package com.sol.expensetrackerfinal.controller;


import com.sol.expensetrackerfinal.dto.UserDTO;
import com.sol.expensetrackerfinal.entities.User;
import com.sol.expensetrackerfinal.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping
@RestController
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody User user) {
        return userService.signup(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody User user) {
        return userService.login(user);
    }

    @GetMapping("/profile")
    public User profile() {
        return userService.profile();
    }

    @GetMapping("/")
    public ResponseEntity<Void> helpController() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/allUsers")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }
}
