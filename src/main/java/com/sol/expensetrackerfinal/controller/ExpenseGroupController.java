package com.sol.expensetrackerfinal.controller;


import com.sol.expensetrackerfinal.dto.AddGroupExpenseRequest;
import com.sol.expensetrackerfinal.dto.CreateGroupRequest;
import com.sol.expensetrackerfinal.dto.GroupWithMemberExpensesDTO;
import com.sol.expensetrackerfinal.entities.ExpenseGroup;
import com.sol.expensetrackerfinal.service.ExpenseGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class ExpenseGroupController {

    private final ExpenseGroupService groupService;
    public ExpenseGroupController(ExpenseGroupService expenseGroupService) {
        this.groupService = expenseGroupService;
    }

    @PostMapping("/create")
    public ExpenseGroup createGroup(@RequestBody CreateGroupRequest request) {
        return groupService.createGroup(request);
    }

    @PostMapping("/add-expense")
    public String addGroupExpense(@RequestBody AddGroupExpenseRequest request) {
        groupService.addGroupExpense(request);
        return "Expense added and split successfully.";
    }

    @GetMapping("/member-expenses/{userId}")
    public ResponseEntity<List<GroupWithMemberExpensesDTO>> getMemberExpenses(@PathVariable Long userId) {
        return ResponseEntity.ok(groupService.getGroupsWithMemberExpenses(userId));
    }

    @DeleteMapping("/delete/{groupId}")
    public ResponseEntity<String> deleteGroup(@PathVariable Long groupId) {
        return groupService.deleteGroup(groupId);
    }
}
