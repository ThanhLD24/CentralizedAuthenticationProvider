package com.esoft.repository;

import com.esoft.domain.AccessToken;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the AccessToken entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {
    Optional<AccessToken> findOneByHashedToken(String hashedToken);
}
