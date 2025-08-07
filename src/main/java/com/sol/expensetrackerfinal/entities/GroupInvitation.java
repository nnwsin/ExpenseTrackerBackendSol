package com.sol.expensetrackerfinal.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_invitation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private boolean accepted = false;

    @ManyToOne
    @JoinColumn(name = "invited_user_id")
    private User invitedUser;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private ExpenseGroup group;

    private LocalDateTime createdAt;
}
