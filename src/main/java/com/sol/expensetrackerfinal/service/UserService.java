package com.sol.expensetrackerfinal.service;

import com.sol.expensetrackerfinal.dto.UserDTO;
import com.sol.expensetrackerfinal.entities.User;
import com.sol.expensetrackerfinal.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public ResponseEntity<Object> signup(User user) {
        if (user.getEmail() == null || user.getUsername() == null || user.getPassword() == null) {
            return new ResponseEntity<>("All Fields are Required", HttpStatus.BAD_REQUEST);
        }

        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            return new ResponseEntity<>("User with " + user.getEmail() + " already exists", HttpStatus.CONFLICT);
        }

        user.setCreatedAt(LocalDate.now());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return new ResponseEntity<>(userRepository.save(user), HttpStatus.CREATED);
    }

    public ResponseEntity<Object> login(User user) {
        User u = userRepository.findByEmail(user.getEmail());
        if (u == null) {
            return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword()
                )
        );

        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(u);
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", token);
            return ResponseEntity.ok(response);
        }
        return new ResponseEntity<>("Invalid login credentials", HttpStatus.UNAUTHORIZED);
    }

    public User profile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()) {
            String email = authentication.getName();
            if (email == null) {
                throw new RuntimeException("User not found");
            }
            return userRepository.findByEmail(email);
        }
        return null;
    }

    public List<UserDTO> getAllUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized user");
        }

        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email);
        List<User> allUsers = userRepository.findByIdNot(currentUser.getId());
        List<UserDTO> result = new ArrayList<>();

        for (User u : allUsers) {
            result.add(new UserDTO(u.getId(), u.getUsername(), u.getEmail()));
        }

        return result;
    }
}
