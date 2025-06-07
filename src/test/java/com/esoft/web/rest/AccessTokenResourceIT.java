package com.esoft.web.rest;

import static com.esoft.domain.AccessTokenAsserts.*;
import static com.esoft.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.esoft.IntegrationTest;
import com.esoft.domain.AccessToken;
import com.esoft.domain.enumeration.TokenStatus;
import com.esoft.repository.AccessTokenRepository;
import com.esoft.service.dto.AccessTokenDTO;
import com.esoft.service.mapper.AccessTokenMapper;
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
 * Integration tests for the {@link AccessTokenResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AccessTokenResourceIT {

    private static final String DEFAULT_HASHED_TOKEN = "AAAAAAAAAA";
    private static final String UPDATED_HASHED_TOKEN = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final TokenStatus DEFAULT_STATUS = TokenStatus.ACTIVE;
    private static final TokenStatus UPDATED_STATUS = TokenStatus.EXPIRED;

    private static final String ENTITY_API_URL = "/api/access-tokens";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    private AccessTokenMapper accessTokenMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAccessTokenMockMvc;

    private AccessToken accessToken;

    private AccessToken insertedAccessToken;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AccessToken createEntity() {
        return new AccessToken()
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
    public static AccessToken createUpdatedEntity() {
        return new AccessToken()
            .hashedToken(UPDATED_HASHED_TOKEN)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        accessToken = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAccessToken != null) {
            accessTokenRepository.delete(insertedAccessToken);
            insertedAccessToken = null;
        }
    }

    @Test
    @Transactional
    void createAccessToken() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AccessToken
        AccessTokenDTO accessTokenDTO = accessTokenMapper.toDto(accessToken);
        var returnedAccessTokenDTO = om.readValue(
            restAccessTokenMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accessTokenDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AccessTokenDTO.class
        );

        // Validate the AccessToken in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAccessToken = accessTokenMapper.toEntity(returnedAccessTokenDTO);
        assertAccessTokenUpdatableFieldsEquals(returnedAccessToken, getPersistedAccessToken(returnedAccessToken));

        insertedAccessToken = returnedAccessToken;
    }

    @Test
    @Transactional
    void createAccessTokenWithExistingId() throws Exception {
        // Create the AccessToken with an existing ID
        accessToken.setId(1L);
        AccessTokenDTO accessTokenDTO = accessTokenMapper.toDto(accessToken);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAccessTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accessTokenDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AccessToken in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAccessTokens() throws Exception {
        // Initialize the database
        insertedAccessToken = accessTokenRepository.saveAndFlush(accessToken);

        // Get all the accessTokenList
        restAccessTokenMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(accessToken.getId().intValue())))
            .andExpect(jsonPath("$.[*].hashedToken").value(hasItem(DEFAULT_HASHED_TOKEN)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].updatedDate").value(hasItem(DEFAULT_UPDATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getAccessToken() throws Exception {
        // Initialize the database
        insertedAccessToken = accessTokenRepository.saveAndFlush(accessToken);

        // Get the accessToken
        restAccessTokenMockMvc
            .perform(get(ENTITY_API_URL_ID, accessToken.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(accessToken.getId().intValue()))
            .andExpect(jsonPath("$.hashedToken").value(DEFAULT_HASHED_TOKEN))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.updatedDate").value(DEFAULT_UPDATED_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingAccessToken() throws Exception {
        // Get the accessToken
        restAccessTokenMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAccessToken() throws Exception {
        // Initialize the database
        insertedAccessToken = accessTokenRepository.saveAndFlush(accessToken);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the accessToken
        AccessToken updatedAccessToken = accessTokenRepository.findById(accessToken.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAccessToken are not directly saved in db
        em.detach(updatedAccessToken);
        updatedAccessToken
            .hashedToken(UPDATED_HASHED_TOKEN)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .status(UPDATED_STATUS);
        AccessTokenDTO accessTokenDTO = accessTokenMapper.toDto(updatedAccessToken);

        restAccessTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, accessTokenDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(accessTokenDTO))
            )
            .andExpect(status().isOk());

        // Validate the AccessToken in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAccessTokenToMatchAllProperties(updatedAccessToken);
    }

    @Test
    @Transactional
    void putNonExistingAccessToken() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        accessToken.setId(longCount.incrementAndGet());

        // Create the AccessToken
        AccessTokenDTO accessTokenDTO = accessTokenMapper.toDto(accessToken);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAccessTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, accessTokenDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(accessTokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccessToken in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAccessToken() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        accessToken.setId(longCount.incrementAndGet());

        // Create the AccessToken
        AccessTokenDTO accessTokenDTO = accessTokenMapper.toDto(accessToken);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccessTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(accessTokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccessToken in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAccessToken() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        accessToken.setId(longCount.incrementAndGet());

        // Create the AccessToken
        AccessTokenDTO accessTokenDTO = accessTokenMapper.toDto(accessToken);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccessTokenMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(accessTokenDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AccessToken in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAccessTokenWithPatch() throws Exception {
        // Initialize the database
        insertedAccessToken = accessTokenRepository.saveAndFlush(accessToken);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the accessToken using partial update
        AccessToken partialUpdatedAccessToken = new AccessToken();
        partialUpdatedAccessToken.setId(accessToken.getId());

        partialUpdatedAccessToken
            .hashedToken(UPDATED_HASHED_TOKEN)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .status(UPDATED_STATUS);

        restAccessTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAccessToken.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAccessToken))
            )
            .andExpect(status().isOk());

        // Validate the AccessToken in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAccessTokenUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAccessToken, accessToken),
            getPersistedAccessToken(accessToken)
        );
    }

    @Test
    @Transactional
    void fullUpdateAccessTokenWithPatch() throws Exception {
        // Initialize the database
        insertedAccessToken = accessTokenRepository.saveAndFlush(accessToken);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the accessToken using partial update
        AccessToken partialUpdatedAccessToken = new AccessToken();
        partialUpdatedAccessToken.setId(accessToken.getId());

        partialUpdatedAccessToken
            .hashedToken(UPDATED_HASHED_TOKEN)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .status(UPDATED_STATUS);

        restAccessTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAccessToken.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAccessToken))
            )
            .andExpect(status().isOk());

        // Validate the AccessToken in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAccessTokenUpdatableFieldsEquals(partialUpdatedAccessToken, getPersistedAccessToken(partialUpdatedAccessToken));
    }

    @Test
    @Transactional
    void patchNonExistingAccessToken() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        accessToken.setId(longCount.incrementAndGet());

        // Create the AccessToken
        AccessTokenDTO accessTokenDTO = accessTokenMapper.toDto(accessToken);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAccessTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, accessTokenDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(accessTokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccessToken in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAccessToken() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        accessToken.setId(longCount.incrementAndGet());

        // Create the AccessToken
        AccessTokenDTO accessTokenDTO = accessTokenMapper.toDto(accessToken);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccessTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(accessTokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccessToken in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAccessToken() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        accessToken.setId(longCount.incrementAndGet());

        // Create the AccessToken
        AccessTokenDTO accessTokenDTO = accessTokenMapper.toDto(accessToken);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccessTokenMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(accessTokenDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AccessToken in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAccessToken() throws Exception {
        // Initialize the database
        insertedAccessToken = accessTokenRepository.saveAndFlush(accessToken);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the accessToken
        restAccessTokenMockMvc
            .perform(delete(ENTITY_API_URL_ID, accessToken.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return accessTokenRepository.count();
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

    protected AccessToken getPersistedAccessToken(AccessToken accessToken) {
        return accessTokenRepository.findById(accessToken.getId()).orElseThrow();
    }

    protected void assertPersistedAccessTokenToMatchAllProperties(AccessToken expectedAccessToken) {
        assertAccessTokenAllPropertiesEquals(expectedAccessToken, getPersistedAccessToken(expectedAccessToken));
    }

    protected void assertPersistedAccessTokenToMatchUpdatableProperties(AccessToken expectedAccessToken) {
        assertAccessTokenAllUpdatablePropertiesEquals(expectedAccessToken, getPersistedAccessToken(expectedAccessToken));
    }
}
