package com.esoft.repository;

import com.esoft.domain.TokenHistory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the TokenHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TokenHistoryRepository extends JpaRepository<TokenHistory, Long> {
    Optional<TokenHistory> findOneByHashedToken(String hashedToken);
}
