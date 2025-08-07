package com.sol.expensetrackerfinal.repository;

import com.sol.expensetrackerfinal.entities.GroupExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupExpenseRepository extends JpaRepository<GroupExpense, Long> {


    @Query("SELECT ge FROM GroupExpense ge JOIN ge.splits s WHERE s.user.id = :userId")
    List<GroupExpense> findBySplitUserId(Long userId);

}
