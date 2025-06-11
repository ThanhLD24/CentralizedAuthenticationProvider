package com.esoft.repository;

import com.esoft.domain.Transaction;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.Instant;

/**
 * Spring Data JPA repository for the Transaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT MAX(t.createdDate) FROM Transaction t WHERE t.userId = :userId")
    Instant findLastTransactionDateByUserId(Long userId);
}
