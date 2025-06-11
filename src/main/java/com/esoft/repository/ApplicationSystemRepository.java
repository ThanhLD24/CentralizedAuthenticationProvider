package com.esoft.repository;

import com.esoft.domain.ApplicationSystem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ApplicationSystem entity.
 *
 * When extending this class, extend ApplicationSystemRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface ApplicationSystemRepository extends JpaRepository<ApplicationSystem, Long> {
    Optional<ApplicationSystem> findByHashedSecretKeyAndActive(String hashedSecretKey, Boolean active);
}
