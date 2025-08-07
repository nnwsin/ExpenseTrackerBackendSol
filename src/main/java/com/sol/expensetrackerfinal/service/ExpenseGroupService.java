package com.sol.expensetrackerfinal.service;

import com.sol.expensetrackerfinal.dto.AddGroupExpenseRequest;
import com.sol.expensetrackerfinal.dto.CreateGroupRequest;
import com.sol.expensetrackerfinal.dto.GroupWithMemberExpensesDTO;
import com.sol.expensetrackerfinal.dto.MemberExpenseDTO;
import com.sol.expensetrackerfinal.entities.*;
import com.sol.expensetrackerfinal.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class ExpenseGroupService {

    private final UserRepository userRepository;
    private final ExpenseGroupRepository groupRepository;
    private final GroupExpenseRepository groupExpenseRepository;
    private final ExpenseRepository expenseRepository;
    private final GroupInvitationRepository groupInvitationRepository;
    private final InvitationService invitationService;

    public ExpenseGroupService(
            UserRepository userRepository,
            ExpenseGroupRepository groupRepository,
            GroupExpenseRepository groupExpenseRepository,
            ExpenseRepository expenseRepository,
            InvitationService invitationService,
            GroupInvitationRepository groupInvitationRepository
    ) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.groupExpenseRepository = groupExpenseRepository;
        this.expenseRepository = expenseRepository;
        this.groupInvitationRepository = groupInvitationRepository;
        this.invitationService = invitationService;
    }

    public ExpenseGroup createGroup(CreateGroupRequest req) {
        User creator = userRepository.findById(req.getCreatedByUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<User> members = new ArrayList<>();
        members.add(creator);

        ExpenseGroup group = new ExpenseGroup();
        group.setName(req.getGroupName());
        group.setCreatedBy(creator);
        group.setMembers(members);
        groupRepository.save(group);

        if (req.getMemberEmails() != null && !req.getMemberEmails().isEmpty()) {
            for (String email : req.getMemberEmails()) {
                User user = userRepository.findByEmail(email);
                if (user == null) {
                    throw new RuntimeException("User does not exist: " + email);
                }
                invitationService.sendGroupInvitation(group.getId(), email);
            }
        }

        return group;
    }

    @Transactional
    public void addGroupExpense(AddGroupExpenseRequest request) {
        ExpenseGroup group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        List<User> members = group.getMembers();
        int totalMembers = members.size();
        if (totalMembers == 0) {
            throw new RuntimeException("No members in the group.");
        }

        Map<Long, BigDecimal> splitMap = request.getSplitMap();
        if (splitMap != null && !splitMap.isEmpty()) {
            for (Map.Entry<Long, BigDecimal> entry : splitMap.entrySet()) {
                userRepository.findById(entry.getKey()).ifPresent(member -> {
                    Expense expense = createExpense(request, group, member, entry.getValue());
                    expenseRepository.save(expense);
                });
            }
        } else {
            BigDecimal perPersonAmount = request.getAmount()
                    .divide(BigDecimal.valueOf(totalMembers), 2, RoundingMode.HALF_UP);
            for (User member : members) {
                Expense expense = createExpense(request, group, member, perPersonAmount);
                expenseRepository.save(expense);
            }
        }

        GroupExpense groupExpense = new GroupExpense();
        groupExpense.setGroup(group);
        groupExpense.setDescription(request.getDescription());
        groupExpense.setAmount(request.getAmount());
        groupExpense.setDate(request.getDate());
        groupExpenseRepository.save(groupExpense);
    }

    private Expense createExpense(AddGroupExpenseRequest request, ExpenseGroup group, User member, BigDecimal amount) {
        Expense expense = new Expense();
        expense.setAmount(amount);
        expense.setName("[Group: " + group.getName() + "] " + request.getDescription());
        expense.setDate(request.getDate());
        expense.setUser(member);
        expense.setCategory(request.getCategory());
        expense.setGroup(group);
        return expense;
    }

    public List<GroupWithMemberExpensesDTO> getGroupsWithMemberExpenses(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("Not Authenticated");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        if (user == null || !user.getId().equals(userId)) {
            throw new RuntimeException("User ID doesn't match the current session's user");
        }

        List<ExpenseGroup> groups = groupRepository.findByMembers_Id(userId);
        List<GroupWithMemberExpensesDTO> result = new ArrayList<>();

        for (ExpenseGroup group : groups) {
            GroupWithMemberExpensesDTO dto = new GroupWithMemberExpensesDTO();
            dto.setGroupId(group.getId());
            dto.setGroupName(group.getName());
            dto.setCreatedById(group.getCreatedBy().getId());

            List<MemberExpenseDTO> memberExpenses = new ArrayList<>();
            for (User member : group.getMembers()) {
                List<Expense> expenses = expenseRepository.findByUser_IdAndGroup_Id(member.getId(), group.getId());
                BigDecimal totalPaid = expenses.stream()
                        .map(Expense::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                MemberExpenseDTO memberExpenseDTO = new MemberExpenseDTO();
                memberExpenseDTO.setMemberId(member.getId());
                memberExpenseDTO.setUsername(member.getUsername());
                memberExpenseDTO.setTotalPaid(totalPaid);

                memberExpenses.add(memberExpenseDTO);
            }

            dto.setMemberExpenses(memberExpenses);
            result.add(dto);
        }

        return result;
    }

    public ResponseEntity<String> deleteGroup(Long groupId) {
        try {
            ExpenseGroup group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                throw new RuntimeException("Not Authenticated");
            }

            String email = authentication.getName();
            User currentUser = userRepository.findByEmail(email);

            if (!group.getCreatedBy().getId().equals(currentUser.getId())) {
                throw new RuntimeException("You are not the admin of the group");
            }

            expenseRepository.deleteAll(expenseRepository.findByGroup_Id(groupId));
            groupInvitationRepository.deleteAll(groupInvitationRepository.findByGroup_Id(groupId));
            groupRepository.deleteById(groupId);

            return ResponseEntity.ok("Deleted group '" + group.getName() + "' successfully");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<String> addAMemberToGroup(Long groupId, Long userId) {
        return ResponseEntity.ok("Not implemented yet");
    }
}
