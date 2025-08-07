package com.sol.expensetrackerfinal.repository;

import com.sol.expensetrackerfinal.entities.ExpenseGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseGroupRepository extends JpaRepository<ExpenseGroup, Long> {

    List<ExpenseGroup> findByMembers_Id(Long userId);

}
