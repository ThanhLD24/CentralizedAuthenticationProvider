package com.esoft.service;

import com.esoft.service.dto.AccessTokenDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.esoft.domain.AccessToken}.
 */
public interface AccessTokenService {
    /**
     * Save a accessToken.
     *
     * @param accessTokenDTO the entity to save.
     * @return the persisted entity.
     */
    AccessTokenDTO save(AccessTokenDTO accessTokenDTO);

    /**
     * Updates a accessToken.
     *
     * @param accessTokenDTO the entity to update.
     * @return the persisted entity.
     */
    AccessTokenDTO update(AccessTokenDTO accessTokenDTO);

    /**
     * Partially updates a accessToken.
     *
     * @param accessTokenDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AccessTokenDTO> partialUpdate(AccessTokenDTO accessTokenDTO);

    /**
     * Get all the accessTokens.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AccessTokenDTO> findAll(Pageable pageable);

    /**
     * Get the "id" accessToken.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AccessTokenDTO> findOne(Long id);

    /**
     * Delete the "id" accessToken.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
