package com.sol.expensetrackerfinal.service;

import com.sol.expensetrackerfinal.dto.GroupInvitationsDTO;
import com.sol.expensetrackerfinal.entities.ExpenseGroup;
import com.sol.expensetrackerfinal.entities.GroupInvitation;
import com.sol.expensetrackerfinal.entities.User;
import com.sol.expensetrackerfinal.repository.ExpenseGroupRepository;
import com.sol.expensetrackerfinal.repository.GroupInvitationRepository;
import com.sol.expensetrackerfinal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class InvitationService {

    @Value("${frontend.url}")
    private String frontendUrl;

    private final GroupInvitationRepository groupInvitationRepository;
    private final ExpenseGroupRepository groupRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    public InvitationService(GroupInvitationRepository groupInvitationRepository,
                             ExpenseGroupRepository groupRepository,
                             UserRepository userRepository,
                             JavaMailSender mailSender) {
        this.groupInvitationRepository = groupInvitationRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    public void sendGroupInvitation(Long groupId, String invitedUserEmail) {
        User invitedUser = userRepository.findByEmail(invitedUserEmail);
        if (invitedUser == null) {
            throw new RuntimeException("Invited user not found");
        }

        ExpenseGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        String token = UUID.randomUUID().toString();
        GroupInvitation invitation = new GroupInvitation();
        invitation.setToken(token);
        invitation.setGroup(group);
        invitation.setInvitedUser(invitedUser);
        invitation.setCreatedAt(LocalDateTime.now());

        groupInvitationRepository.save(invitation);

        String confirmationLink = frontendUrl + "/groups/invites";

        String subject = "Group Invitation: " + group.getName();
        String body = "You have been invited to join the group \"" + group.getName() + "\".\n"
                + "Click the link to confirm: " + confirmationLink;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(invitedUser.getEmail());
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public ResponseEntity<String> confirmGroupInvitation(String token) {
        GroupInvitation invitation = groupInvitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));

        if (invitation.isAccepted()) {
            return ResponseEntity.ok("You already accepted this invitation.");
        }

        ExpenseGroup group = invitation.getGroup();
        User invitedUser = invitation.getInvitedUser();

        group.getMembers().add(invitedUser);
        invitation.setAccepted(true);

        groupRepository.save(group);
        groupInvitationRepository.save(invitation);

        return ResponseEntity.ok("Successfully joined group: " + group.getName());
    }

    public ResponseEntity<List<GroupInvitationsDTO>> getAllInvitations() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!authentication.isAuthenticated()) {
                throw new RuntimeException("Not Authenticated");
            }

            String email = authentication.getName();
            User user = userRepository.findByEmail(email);

            List<GroupInvitation> invitations = groupInvitationRepository.findByInvitedUser_Id(user.getId());
            List<GroupInvitationsDTO> resultList = new ArrayList<>();

            for (GroupInvitation invitation : invitations) {
                resultList.add(getGroupInvitationsDTO(invitation));
            }

            return new ResponseEntity<>(resultList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private static GroupInvitationsDTO getGroupInvitationsDTO(GroupInvitation invitation) {
        return new GroupInvitationsDTO(
                invitation.getId(),
                invitation.getToken(),
                invitation.getGroup().getName(),
                invitation.getGroup().getCreatedBy().getUsername(),
                invitation.getGroup().getCreatedBy().getEmail(),
                invitation.isAccepted(),
                invitation.getCreatedAt()
        );
    }

    public ResponseEntity<String> declineInvitation(Long invitationId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!authentication.isAuthenticated()) {
                throw new RuntimeException("Not Authenticated");
            }

            String email = authentication.getName();
            User user = userRepository.findByEmail(email);

            Optional<GroupInvitation> invitationOpt = groupInvitationRepository.findById(invitationId);
            if (invitationOpt.isEmpty()) {
                throw new RuntimeException("No Invitation Found");
            }

            GroupInvitation invitation = invitationOpt.get();
            if (!Objects.equals(invitation.getInvitedUser().getId(), user.getId())) {
                throw new RuntimeException("You cannot delete another user's invitation");
            }

            groupInvitationRepository.deleteById(invitationId);
            return new ResponseEntity<>("Invitation Declined", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
