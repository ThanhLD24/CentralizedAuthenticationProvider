package com.esoft.web.rest;

import static com.esoft.domain.TokenHistoryAsserts.*;
import static com.esoft.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.esoft.IntegrationTest;
import com.esoft.domain.TokenHistory;
import com.esoft.domain.enumeration.TokenStatus;
import com.esoft.repository.TokenHistoryRepository;
import com.esoft.service.dto.TokenHistoryDTO;
import com.esoft.service.mapper.TokenHistoryMapper;
import com.esoft.web.rest.web.TokenHistoryResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TokenHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TokenHistoryResourceIT {

    private static final String DEFAULT_HASHED_TOKEN = "AAAAAAAAAA";
    private static final String UPDATED_HASHED_TOKEN = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final TokenStatus DEFAULT_STATUS = TokenStatus.ACTIVE;
    private static final TokenStatus UPDATED_STATUS = TokenStatus.EXPIRED;

    private static final String ENTITY_API_URL = "/api/token-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TokenHistoryRepository tokenHistoryRepository;

    @Autowired
    private TokenHistoryMapper tokenHistoryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTokenHistoryMockMvc;

    private TokenHistory tokenHistory;

    private TokenHistory insertedTokenHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TokenHistory createEntity() {
        return new TokenHistory()
            .hashedToken(DEFAULT_HASHED_TOKEN)
            .createdDate(DEFAULT_CREATED_DATE)
            .updatedDate(DEFAULT_UPDATED_DATE)
            .status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TokenHistory createUpdatedEntity() {
        return new TokenHistory()
            .hashedToken(UPDATED_HASHED_TOKEN)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        tokenHistory = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTokenHistory != null) {
            tokenHistoryRepository.delete(insertedTokenHistory);
            insertedTokenHistory = null;
        }
    }

    @Test
    @Transactional
    void createTokenHistory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TokenHistory
        TokenHistoryDTO tokenHistoryDTO = tokenHistoryMapper.toDto(tokenHistory);
        var returnedTokenHistoryDTO = om.readValue(
            restTokenHistoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tokenHistoryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TokenHistoryDTO.class
        );

        // Validate the TokenHistory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTokenHistory = tokenHistoryMapper.toEntity(returnedTokenHistoryDTO);
        assertTokenHistoryUpdatableFieldsEquals(returnedTokenHistory, getPersistedTokenHistory(returnedTokenHistory));

        insertedTokenHistory = returnedTokenHistory;
    }

    @Test
    @Transactional
    void createTokenHistoryWithExistingId() throws Exception {
        // Create the TokenHistory with an existing ID
        tokenHistory.setId(1L);
        TokenHistoryDTO tokenHistoryDTO = tokenHistoryMapper.toDto(tokenHistory);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTokenHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tokenHistoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TokenHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTokenHistories() throws Exception {
        // Initialize the database
        insertedTokenHistory = tokenHistoryRepository.saveAndFlush(tokenHistory);

        // Get all the tokenHistoryList
        restTokenHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tokenHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].hashedToken").value(hasItem(DEFAULT_HASHED_TOKEN)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].updatedDate").value(hasItem(DEFAULT_UPDATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getTokenHistory() throws Exception {
        // Initialize the database
        insertedTokenHistory = tokenHistoryRepository.saveAndFlush(tokenHistory);

        // Get the tokenHistory
        restTokenHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, tokenHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tokenHistory.getId().intValue()))
            .andExpect(jsonPath("$.hashedToken").value(DEFAULT_HASHED_TOKEN))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.updatedDate").value(DEFAULT_UPDATED_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTokenHistory() throws Exception {
        // Get the tokenHistory
        restTokenHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTokenHistory() throws Exception {
        // Initialize the database
        insertedTokenHistory = tokenHistoryRepository.saveAndFlush(tokenHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tokenHistory
        TokenHistory updatedTokenHistory = tokenHistoryRepository.findById(tokenHistory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTokenHistory are not directly saved in db
        em.detach(updatedTokenHistory);
        updatedTokenHistory
            .hashedToken(UPDATED_HASHED_TOKEN)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .status(UPDATED_STATUS);
        TokenHistoryDTO tokenHistoryDTO = tokenHistoryMapper.toDto(updatedTokenHistory);

        restTokenHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tokenHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tokenHistoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the TokenHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTokenHistoryToMatchAllProperties(updatedTokenHistory);
    }

    @Test
    @Transactional
    void putNonExistingTokenHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tokenHistory.setId(longCount.incrementAndGet());

        // Create the TokenHistory
        TokenHistoryDTO tokenHistoryDTO = tokenHistoryMapper.toDto(tokenHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTokenHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tokenHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tokenHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TokenHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTokenHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tokenHistory.setId(longCount.incrementAndGet());

        // Create the TokenHistory
        TokenHistoryDTO tokenHistoryDTO = tokenHistoryMapper.toDto(tokenHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTokenHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tokenHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TokenHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTokenHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tokenHistory.setId(longCount.incrementAndGet());

        // Create the TokenHistory
        TokenHistoryDTO tokenHistoryDTO = tokenHistoryMapper.toDto(tokenHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTokenHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tokenHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TokenHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTokenHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedTokenHistory = tokenHistoryRepository.saveAndFlush(tokenHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tokenHistory using partial update
        TokenHistory partialUpdatedTokenHistory = new TokenHistory();
        partialUpdatedTokenHistory.setId(tokenHistory.getId());

        partialUpdatedTokenHistory.hashedToken(UPDATED_HASHED_TOKEN).updatedDate(UPDATED_UPDATED_DATE);

        restTokenHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTokenHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTokenHistory))
            )
            .andExpect(status().isOk());

        // Validate the TokenHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTokenHistoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTokenHistory, tokenHistory),
            getPersistedTokenHistory(tokenHistory)
        );
    }

    @Test
    @Transactional
    void fullUpdateTokenHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedTokenHistory = tokenHistoryRepository.saveAndFlush(tokenHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tokenHistory using partial update
        TokenHistory partialUpdatedTokenHistory = new TokenHistory();
        partialUpdatedTokenHistory.setId(tokenHistory.getId());

        partialUpdatedTokenHistory
            .hashedToken(UPDATED_HASHED_TOKEN)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .status(UPDATED_STATUS);

        restTokenHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTokenHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTokenHistory))
            )
            .andExpect(status().isOk());

        // Validate the TokenHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTokenHistoryUpdatableFieldsEquals(partialUpdatedTokenHistory, getPersistedTokenHistory(partialUpdatedTokenHistory));
    }

    @Test
    @Transactional
    void patchNonExistingTokenHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tokenHistory.setId(longCount.incrementAndGet());

        // Create the TokenHistory
        TokenHistoryDTO tokenHistoryDTO = tokenHistoryMapper.toDto(tokenHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTokenHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tokenHistoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tokenHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TokenHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTokenHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tokenHistory.setId(longCount.incrementAndGet());

        // Create the TokenHistory
        TokenHistoryDTO tokenHistoryDTO = tokenHistoryMapper.toDto(tokenHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTokenHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tokenHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TokenHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTokenHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tokenHistory.setId(longCount.incrementAndGet());

        // Create the TokenHistory
        TokenHistoryDTO tokenHistoryDTO = tokenHistoryMapper.toDto(tokenHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTokenHistoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(tokenHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TokenHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTokenHistory() throws Exception {
        // Initialize the database
        insertedTokenHistory = tokenHistoryRepository.saveAndFlush(tokenHistory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the tokenHistory
        restTokenHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, tokenHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tokenHistoryRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected TokenHistory getPersistedTokenHistory(TokenHistory tokenHistory) {
        return tokenHistoryRepository.findById(tokenHistory.getId()).orElseThrow();
    }

    protected void assertPersistedTokenHistoryToMatchAllProperties(TokenHistory expectedTokenHistory) {
        assertTokenHistoryAllPropertiesEquals(expectedTokenHistory, getPersistedTokenHistory(expectedTokenHistory));
    }

    protected void assertPersistedTokenHistoryToMatchUpdatableProperties(TokenHistory expectedTokenHistory) {
        assertTokenHistoryAllUpdatablePropertiesEquals(expectedTokenHistory, getPersistedTokenHistory(expectedTokenHistory));
    }
}
