package com.sol.expensetrackerfinal.service;

import com.sol.expensetrackerfinal.entities.Expense;
import com.sol.expensetrackerfinal.entities.User;
import com.sol.expensetrackerfinal.repository.ExpenseRepository;
import com.sol.expensetrackerfinal.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    public Expense addExpense(Expense expense) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("USER NOT FOUND... Login Again");
        }

        if (expense.getName() == null || expense.getAmount() == null ||
                expense.getCategory() == null || expense.getDate() == null) {
            throw new RuntimeException("Some fields are empty");
        }

        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    public ResponseEntity<List<Expense>> getAllExpenses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("USER NOT FOUND... Login Again");
        }

        return ResponseEntity.ok(user.getExpenses());
    }

    public ResponseEntity<Object> deleteExpense(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>("User not Authorized", HttpStatus.UNAUTHORIZED);
        }

        if (expenseRepository.existsById(id) &&
                expenseRepository.findUserByExpenseId(id).getEmail().equals(authentication.getName())) {
            expenseRepository.deleteById(id);
            return ResponseEntity.ok("Expense deleted successfully");
        } else {
            return new ResponseEntity<>("Expense doesn't exist", HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Object> updateExpense(Long id, Expense updatedExpense) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return new ResponseEntity<>("User not Authorized", HttpStatus.UNAUTHORIZED);
        }

        Optional<Expense> optionalExpense = expenseRepository.findById(id);
        if (optionalExpense.isEmpty() ||
                !expenseRepository.findUserByExpenseId(id).getEmail().equals(authentication.getName())) {
            return new ResponseEntity<>("Expense doesn't exist", HttpStatus.NOT_FOUND);
        }

        Expense expense = optionalExpense.get();
        expense.setAmount(updatedExpense.getAmount());
        expense.setCategory(updatedExpense.getCategory());
        expense.setDate(updatedExpense.getDate());
        expense.setName(updatedExpense.getName());

        expenseRepository.save(expense);
        return new ResponseEntity<>("Expense updated successfully", HttpStatus.OK);
    }
}
