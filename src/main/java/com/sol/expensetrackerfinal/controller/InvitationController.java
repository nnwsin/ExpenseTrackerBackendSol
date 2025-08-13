package com.sol.expensetrackerfinal.controller;


import com.sol.expensetrackerfinal.dto.GroupInvitationsDTO;
import com.sol.expensetrackerfinal.service.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/group/invite")
public class InvitationController {

    private final InvitationService invitationService;
    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @GetMapping("/confirm-invitation")
    public ResponseEntity<String> confirmGroupInvitation(@RequestParam String token) {
        return invitationService.confirmGroupInvitation(token);
    }

    @GetMapping("/getall")
    public ResponseEntity<List<GroupInvitationsDTO>> getAllInvitations() {
        return invitationService.getAllInvitations();
    }

    @DeleteMapping("/decline/{id}")
    public ResponseEntity<String> declineInvitation(@PathVariable Long id) {
        return invitationService.declineInvitation(id);
    }
}
