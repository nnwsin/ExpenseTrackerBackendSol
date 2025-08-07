package com.sol.expensetrackerfinal.service;

import com.sol.expensetrackerfinal.entities.User;
import com.sol.expensetrackerfinal.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        return new CustomUserDetails(user);
    }
}
