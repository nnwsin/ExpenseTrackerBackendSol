package com.sol.expensetrackerfinal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddGroupExpenseRequest {
    private Long groupId;
    private String description;
    private BigDecimal amount;
    private LocalDateTime date;
    private String category;
    private Map<Long, BigDecimal> splitMap;
}
