package com.sol.expensetrackerfinal.dto;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class GroupInvitationsDTO {
    private Long id;
    private String token;
    private String groupName;
    private String createdBy;
    private String createdByEmail;
    private boolean isAccepted;
    private LocalDateTime invitedAt;

}
