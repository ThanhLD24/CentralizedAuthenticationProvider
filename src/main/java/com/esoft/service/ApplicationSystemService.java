package com.esoft.service;

import com.esoft.service.dto.ApplicationSystemDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.esoft.domain.ApplicationSystem}.
 */
public interface ApplicationSystemService {
    /**
     * Save a applicationSystem.
     *
     * @param applicationSystemDTO the entity to save.
     * @return the persisted entity.
     */
    ApplicationSystemDTO save(ApplicationSystemDTO applicationSystemDTO);

    /**
     * Updates a applicationSystem.
     *
     * @param applicationSystemDTO the entity to update.
     * @return the persisted entity.
     */
    ApplicationSystemDTO update(ApplicationSystemDTO applicationSystemDTO);

    /**
     * Partially updates a applicationSystem.
     *
     * @param applicationSystemDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ApplicationSystemDTO> partialUpdate(ApplicationSystemDTO applicationSystemDTO);

    /**
     * Get all the applicationSystems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ApplicationSystemDTO> findAll(Pageable pageable);

    /**
     * Get all the applicationSystems with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ApplicationSystemDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" applicationSystem.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ApplicationSystemDTO> findOne(Long id);

    /**
     * Delete the "id" applicationSystem.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
