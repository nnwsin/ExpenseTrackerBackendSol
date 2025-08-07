package com.sol.expensetrackerfinal.repository;

import com.sol.expensetrackerfinal.entities.Split;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SplitRepository extends JpaRepository<Split, Long> {
}
