package com.esoft.service;

import com.esoft.service.dto.TokenHistoryDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.esoft.domain.TokenHistory}.
 */
public interface TokenHistoryService {
    /**
     * Save a tokenHistory.
     *
     * @param tokenHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    TokenHistoryDTO save(TokenHistoryDTO tokenHistoryDTO);

    /**
     * Updates a tokenHistory.
     *
     * @param tokenHistoryDTO the entity to update.
     * @return the persisted entity.
     */
    TokenHistoryDTO update(TokenHistoryDTO tokenHistoryDTO);

    /**
     * Partially updates a tokenHistory.
     *
     * @param tokenHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TokenHistoryDTO> partialUpdate(TokenHistoryDTO tokenHistoryDTO);

    /**
     * Get all the tokenHistorys.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TokenHistoryDTO> findAll(Pageable pageable);

    /**
     * Get the "id" tokenHistory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TokenHistoryDTO> findOne(Long id);

    /**
     * Delete the "id" tokenHistory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
