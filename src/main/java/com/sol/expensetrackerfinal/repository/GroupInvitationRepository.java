package com.sol.expensetrackerfinal.repository;

import com.sol.expensetrackerfinal.entities.GroupInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long> {

    // Find invitation by token
    Optional<GroupInvitation> findByToken(String token);

    // Get all invitations for a specific group
    List<GroupInvitation> findByGroup_Id(Long groupId);

    // Get all invitations sent to a specific user
    List<GroupInvitation> findByInvitedUser_Id(Long userId);

    // Delete an invitation by ID
    void deleteById(Long id);
}
