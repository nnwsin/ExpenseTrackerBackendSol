package com.sol.expensetrackerfinal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberExpenseDTO {
    private Long memberId;
    private String username;
    private BigDecimal totalPaid;
}
