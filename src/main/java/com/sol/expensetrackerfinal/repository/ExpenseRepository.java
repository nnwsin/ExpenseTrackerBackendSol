package com.sol.expensetrackerfinal.repository;

import com.sol.expensetrackerfinal.entities.Expense;
import com.sol.expensetrackerfinal.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUser_Id(Long userId);

    List<Expense> findByGroup_Id(Long groupId);

    List<Expense> findByUser_IdAndGroup_Id(Long userId, Long groupId); // ✅ FIXED

    @Query("SELECT e.user FROM Expense e WHERE e.id = :expenseId") // ✅ FIXED
    User findUserByExpenseId(Long expenseId);
}
