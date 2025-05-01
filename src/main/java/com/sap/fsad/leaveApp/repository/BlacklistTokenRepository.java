package com.sap.fsad.leaveApp.repository;

import com.sap.fsad.leaveApp.model.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    boolean existsByToken(String token);
}