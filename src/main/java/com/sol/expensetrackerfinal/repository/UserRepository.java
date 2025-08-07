package com.sol.expensetrackerfinal.repository;

import com.sol.expensetrackerfinal.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email
    User findByEmail(String email);

    // Find all users except the one with the given ID
    List<User> findByIdNot(Long id);
}
