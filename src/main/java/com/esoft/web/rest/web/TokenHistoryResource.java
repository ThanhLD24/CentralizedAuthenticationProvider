package com.esoft.web.rest.web;

import com.esoft.repository.TokenHistoryRepository;
import com.esoft.service.TokenHistoryService;
import com.esoft.service.dto.TokenHistoryDTO;
import com.esoft.service.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.esoft.domain.TokenHistory}.
 */
@RestController
@RequestMapping("/api/token-histories")
public class TokenHistoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(TokenHistoryResource.class);

    private static final String ENTITY_NAME = "tokenHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TokenHistoryService tokenHistoryService;

    private final TokenHistoryRepository tokenHistoryRepository;

    public TokenHistoryResource(TokenHistoryService tokenHistoryService, TokenHistoryRepository tokenHistoryRepository) {
        this.tokenHistoryService = tokenHistoryService;
        this.tokenHistoryRepository = tokenHistoryRepository;
    }

    /**
     * {@code POST  /token-histories} : Create a new tokenHistory.
     *
     * @param tokenHistoryDTO the tokenHistoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tokenHistoryDTO, or with status {@code 400 (Bad Request)} if the tokenHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TokenHistoryDTO> createTokenHistory(@RequestBody TokenHistoryDTO tokenHistoryDTO) throws URISyntaxException {
        LOG.debug("REST request to save TokenHistory : {}", tokenHistoryDTO);
        if (tokenHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new tokenHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        tokenHistoryDTO = tokenHistoryService.save(tokenHistoryDTO);
        return ResponseEntity.created(new URI("/api/token-histories/" + tokenHistoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, tokenHistoryDTO.getId().toString()))
            .body(tokenHistoryDTO);
    }

    /**
     * {@code PUT  /token-histories/:id} : Updates an existing tokenHistory.
     *
     * @param id the id of the tokenHistoryDTO to save.
     * @param tokenHistoryDTO the tokenHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tokenHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the tokenHistoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tokenHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TokenHistoryDTO> updateTokenHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TokenHistoryDTO tokenHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TokenHistory : {}, {}", id, tokenHistoryDTO);
        if (tokenHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tokenHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tokenHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        tokenHistoryDTO = tokenHistoryService.update(tokenHistoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tokenHistoryDTO.getId().toString()))
            .body(tokenHistoryDTO);
    }

    /**
     * {@code PATCH  /token-histories/:id} : Partial updates given fields of an existing tokenHistory, field will ignore if it is null
     *
     * @param id the id of the tokenHistoryDTO to save.
     * @param tokenHistoryDTO the tokenHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tokenHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the tokenHistoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tokenHistoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tokenHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TokenHistoryDTO> partialUpdateTokenHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TokenHistoryDTO tokenHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TokenHistory partially : {}, {}", id, tokenHistoryDTO);
        if (tokenHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tokenHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tokenHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TokenHistoryDTO> result = tokenHistoryService.partialUpdate(tokenHistoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tokenHistoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /token-histories} : get all the tokenHistories.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tokenHistories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TokenHistoryDTO>> getAllTokenHistories(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of TokenHistories");
        Page<TokenHistoryDTO> page = tokenHistoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /token-histories/:id} : get the "id" tokenHistory.
     *
     * @param id the id of the tokenHistoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tokenHistoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TokenHistoryDTO> getTokenHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TokenHistory : {}", id);
        Optional<TokenHistoryDTO> tokenHistoryDTO = tokenHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tokenHistoryDTO);
    }

    /**
     * {@code DELETE  /token-histories/:id} : delete the "id" tokenHistory.
     *
     * @param id the id of the tokenHistoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTokenHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TokenHistory : {}", id);
        tokenHistoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
