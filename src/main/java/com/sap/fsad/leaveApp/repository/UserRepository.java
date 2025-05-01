package com.sap.fsad.leaveApp.repository;

import com.sap.fsad.leaveApp.model.User;
import com.sap.fsad.leaveApp.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    List<User> findByManager(User manager);

    List<User> findByManagerId(Long managerId);

    List<User> findByDepartment(String department);

    @Query("SELECT u FROM User u WHERE :role MEMBER OF u.roles")
    List<User> findByRole(@Param("role") UserRole role);

    List<User> findByIsActiveTrue();

    Optional<User> findByResetToken(String resetToken);
}
