package com.esoft.web.rest;

import com.esoft.repository.AccessTokenRepository;
import com.esoft.service.AccessTokenService;
import com.esoft.service.dto.AccessTokenDTO;
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
 * REST controller for managing {@link com.esoft.domain.AccessToken}.
 */
@RestController
@RequestMapping("/api/access-tokens")
public class AccessTokenResource {

    private static final Logger LOG = LoggerFactory.getLogger(AccessTokenResource.class);

    private static final String ENTITY_NAME = "accessToken";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AccessTokenService accessTokenService;

    private final AccessTokenRepository accessTokenRepository;

    public AccessTokenResource(AccessTokenService accessTokenService, AccessTokenRepository accessTokenRepository) {
        this.accessTokenService = accessTokenService;
        this.accessTokenRepository = accessTokenRepository;
    }

    /**
     * {@code POST  /access-tokens} : Create a new accessToken.
     *
     * @param accessTokenDTO the accessTokenDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new accessTokenDTO, or with status {@code 400 (Bad Request)} if the accessToken has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AccessTokenDTO> createAccessToken(@RequestBody AccessTokenDTO accessTokenDTO) throws URISyntaxException {
        LOG.debug("REST request to save AccessToken : {}", accessTokenDTO);
        if (accessTokenDTO.getId() != null) {
            throw new BadRequestAlertException("A new accessToken cannot already have an ID", ENTITY_NAME, "idexists");
        }
        accessTokenDTO = accessTokenService.save(accessTokenDTO);
        return ResponseEntity.created(new URI("/api/access-tokens/" + accessTokenDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, accessTokenDTO.getId().toString()))
            .body(accessTokenDTO);
    }

    /**
     * {@code PUT  /access-tokens/:id} : Updates an existing accessToken.
     *
     * @param id the id of the accessTokenDTO to save.
     * @param accessTokenDTO the accessTokenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated accessTokenDTO,
     * or with status {@code 400 (Bad Request)} if the accessTokenDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the accessTokenDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AccessTokenDTO> updateAccessToken(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AccessTokenDTO accessTokenDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AccessToken : {}, {}", id, accessTokenDTO);
        if (accessTokenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, accessTokenDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!accessTokenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        accessTokenDTO = accessTokenService.update(accessTokenDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, accessTokenDTO.getId().toString()))
            .body(accessTokenDTO);
    }

    /**
     * {@code PATCH  /access-tokens/:id} : Partial updates given fields of an existing accessToken, field will ignore if it is null
     *
     * @param id the id of the accessTokenDTO to save.
     * @param accessTokenDTO the accessTokenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated accessTokenDTO,
     * or with status {@code 400 (Bad Request)} if the accessTokenDTO is not valid,
     * or with status {@code 404 (Not Found)} if the accessTokenDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the accessTokenDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AccessTokenDTO> partialUpdateAccessToken(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AccessTokenDTO accessTokenDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AccessToken partially : {}, {}", id, accessTokenDTO);
        if (accessTokenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, accessTokenDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!accessTokenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AccessTokenDTO> result = accessTokenService.partialUpdate(accessTokenDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, accessTokenDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /access-tokens} : get all the accessTokens.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of accessTokens in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AccessTokenDTO>> getAllAccessTokens(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of AccessTokens");
        Page<AccessTokenDTO> page = accessTokenService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /access-tokens/:id} : get the "id" accessToken.
     *
     * @param id the id of the accessTokenDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the accessTokenDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccessTokenDTO> getAccessToken(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AccessToken : {}", id);
        Optional<AccessTokenDTO> accessTokenDTO = accessTokenService.findOne(id);
        return ResponseUtil.wrapOrNotFound(accessTokenDTO);
    }

    /**
     * {@code DELETE  /access-tokens/:id} : delete the "id" accessToken.
     *
     * @param id the id of the accessTokenDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccessToken(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AccessToken : {}", id);
        accessTokenService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
