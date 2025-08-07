package com.sol.expensetrackerfinal.dto;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class CreateGroupRequest {
    private String groupName;
    private Long createdByUserId;
    private List<String> memberEmails;
}