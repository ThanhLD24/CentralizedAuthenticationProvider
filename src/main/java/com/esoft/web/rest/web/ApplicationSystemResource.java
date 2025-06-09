package com.esoft.web.rest.web;

import com.esoft.repository.ApplicationSystemRepository;
import com.esoft.service.ApplicationSystemService;
import com.esoft.service.dto.ApplicationSystemDTO;
import com.esoft.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.esoft.domain.ApplicationSystem}.
 */
@RestController
@RequestMapping("/api/application-systems")
public class ApplicationSystemResource {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationSystemResource.class);

    private static final String ENTITY_NAME = "applicationSystem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ApplicationSystemService applicationSystemService;

    private final ApplicationSystemRepository applicationSystemRepository;

    public ApplicationSystemResource(
        ApplicationSystemService applicationSystemService,
        ApplicationSystemRepository applicationSystemRepository
    ) {
        this.applicationSystemService = applicationSystemService;
        this.applicationSystemRepository = applicationSystemRepository;
    }

    /**
     * {@code POST  /application-systems} : Create a new applicationSystem.
     *
     * @param applicationSystemDTO the applicationSystemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new applicationSystemDTO, or with status {@code 400 (Bad Request)} if the applicationSystem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ApplicationSystemDTO> createApplicationSystem(@RequestBody ApplicationSystemDTO applicationSystemDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ApplicationSystem : {}", applicationSystemDTO);
        if (applicationSystemDTO.getId() != null) {
            throw new BadRequestAlertException("A new applicationSystem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        applicationSystemDTO = applicationSystemService.save(applicationSystemDTO);
        return ResponseEntity.created(new URI("/api/application-systems/" + applicationSystemDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, applicationSystemDTO.getId().toString()))
            .body(applicationSystemDTO);
    }

    /**
     * {@code PUT  /application-systems/:id} : Updates an existing applicationSystem.
     *
     * @param id the id of the applicationSystemDTO to save.
     * @param applicationSystemDTO the applicationSystemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated applicationSystemDTO,
     * or with status {@code 400 (Bad Request)} if the applicationSystemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the applicationSystemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApplicationSystemDTO> updateApplicationSystem(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ApplicationSystemDTO applicationSystemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ApplicationSystem : {}, {}", id, applicationSystemDTO);
        if (applicationSystemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, applicationSystemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!applicationSystemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        applicationSystemDTO = applicationSystemService.update(applicationSystemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, applicationSystemDTO.getId().toString()))
            .body(applicationSystemDTO);
    }

    /**
     * {@code PATCH  /application-systems/:id} : Partial updates given fields of an existing applicationSystem, field will ignore if it is null
     *
     * @param id the id of the applicationSystemDTO to save.
     * @param applicationSystemDTO the applicationSystemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated applicationSystemDTO,
     * or with status {@code 400 (Bad Request)} if the applicationSystemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the applicationSystemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the applicationSystemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ApplicationSystemDTO> partialUpdateApplicationSystem(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ApplicationSystemDTO applicationSystemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ApplicationSystem partially : {}, {}", id, applicationSystemDTO);
        if (applicationSystemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, applicationSystemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!applicationSystemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ApplicationSystemDTO> result = applicationSystemService.partialUpdate(applicationSystemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, applicationSystemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /application-systems} : get all the applicationSystems.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of applicationSystems in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ApplicationSystemDTO>> getAllApplicationSystems(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of ApplicationSystems");
        Page<ApplicationSystemDTO> page = applicationSystemService.findAll(pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /application-systems/:id} : get the "id" applicationSystem.
     *
     * @param id the id of the applicationSystemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the applicationSystemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationSystemDTO> getApplicationSystem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ApplicationSystem : {}", id);
        Optional<ApplicationSystemDTO> applicationSystemDTO = applicationSystemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(applicationSystemDTO);
    }

    /**
     * {@code DELETE  /application-systems/:id} : delete the "id" applicationSystem.
     *
     * @param id the id of the applicationSystemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplicationSystem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ApplicationSystem : {}", id);
        applicationSystemService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
