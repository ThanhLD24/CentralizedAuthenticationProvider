package com.esoft.web.rest;

import static com.esoft.domain.ApplicationSystemAsserts.*;
import static com.esoft.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.esoft.IntegrationTest;
import com.esoft.domain.ApplicationSystem;
import com.esoft.repository.ApplicationSystemRepository;
import com.esoft.repository.UserRepository;
import com.esoft.service.ApplicationSystemService;
import com.esoft.service.dto.ApplicationSystemDTO;
import com.esoft.service.mapper.ApplicationSystemMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ApplicationSystemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ApplicationSystemResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String DEFAULT_HASHED_SECRET_KEY = "AAAAAAAAAA";
    private static final String UPDATED_HASHED_SECRET_KEY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/application-systems";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ApplicationSystemRepository applicationSystemRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ApplicationSystemRepository applicationSystemRepositoryMock;

    @Autowired
    private ApplicationSystemMapper applicationSystemMapper;

    @Mock
    private ApplicationSystemService applicationSystemServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restApplicationSystemMockMvc;

    private ApplicationSystem applicationSystem;

    private ApplicationSystem insertedApplicationSystem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApplicationSystem createEntity() {
        return new ApplicationSystem()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .createdDate(DEFAULT_CREATED_DATE)
            .updatedDate(DEFAULT_UPDATED_DATE)
            .active(DEFAULT_ACTIVE)
            .hashedSecretKey(DEFAULT_HASHED_SECRET_KEY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ApplicationSystem createUpdatedEntity() {
        return new ApplicationSystem()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .active(UPDATED_ACTIVE)
            .hashedSecretKey(UPDATED_HASHED_SECRET_KEY);
    }

    @BeforeEach
    void initTest() {
        applicationSystem = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedApplicationSystem != null) {
            applicationSystemRepository.delete(insertedApplicationSystem);
            insertedApplicationSystem = null;
        }
    }

    @Test
    @Transactional
    void createApplicationSystem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ApplicationSystem
        ApplicationSystemDTO applicationSystemDTO = applicationSystemMapper.toDto(applicationSystem);
        var returnedApplicationSystemDTO = om.readValue(
            restApplicationSystemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(applicationSystemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ApplicationSystemDTO.class
        );

        // Validate the ApplicationSystem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedApplicationSystem = applicationSystemMapper.toEntity(returnedApplicationSystemDTO);
        assertApplicationSystemUpdatableFieldsEquals(returnedApplicationSystem, getPersistedApplicationSystem(returnedApplicationSystem));

        insertedApplicationSystem = returnedApplicationSystem;
    }

    @Test
    @Transactional
    void createApplicationSystemWithExistingId() throws Exception {
        // Create the ApplicationSystem with an existing ID
        applicationSystem.setId(1L);
        ApplicationSystemDTO applicationSystemDTO = applicationSystemMapper.toDto(applicationSystem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restApplicationSystemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(applicationSystemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ApplicationSystem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllApplicationSystems() throws Exception {
        // Initialize the database
        insertedApplicationSystem = applicationSystemRepository.saveAndFlush(applicationSystem);

        // Get all the applicationSystemList
        restApplicationSystemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(applicationSystem.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].updatedDate").value(hasItem(DEFAULT_UPDATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].hashedSecretKey").value(hasItem(DEFAULT_HASHED_SECRET_KEY)));
    }




    @Test
    @Transactional
    void getApplicationSystem() throws Exception {
        // Initialize the database
        insertedApplicationSystem = applicationSystemRepository.saveAndFlush(applicationSystem);

        // Get the applicationSystem
        restApplicationSystemMockMvc
            .perform(get(ENTITY_API_URL_ID, applicationSystem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(applicationSystem.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.updatedDate").value(DEFAULT_UPDATED_DATE.toString()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
            .andExpect(jsonPath("$.hashedSecretKey").value(DEFAULT_HASHED_SECRET_KEY));
    }

    @Test
    @Transactional
    void getNonExistingApplicationSystem() throws Exception {
        // Get the applicationSystem
        restApplicationSystemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingApplicationSystem() throws Exception {
        // Initialize the database
        insertedApplicationSystem = applicationSystemRepository.saveAndFlush(applicationSystem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the applicationSystem
        ApplicationSystem updatedApplicationSystem = applicationSystemRepository.findById(applicationSystem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedApplicationSystem are not directly saved in db
        em.detach(updatedApplicationSystem);
        updatedApplicationSystem
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .active(UPDATED_ACTIVE)
            .hashedSecretKey(UPDATED_HASHED_SECRET_KEY);
        ApplicationSystemDTO applicationSystemDTO = applicationSystemMapper.toDto(updatedApplicationSystem);

        restApplicationSystemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, applicationSystemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(applicationSystemDTO))
            )
            .andExpect(status().isOk());

        // Validate the ApplicationSystem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedApplicationSystemToMatchAllProperties(updatedApplicationSystem);
    }

    @Test
    @Transactional
    void putNonExistingApplicationSystem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        applicationSystem.setId(longCount.incrementAndGet());

        // Create the ApplicationSystem
        ApplicationSystemDTO applicationSystemDTO = applicationSystemMapper.toDto(applicationSystem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApplicationSystemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, applicationSystemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(applicationSystemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationSystem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchApplicationSystem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        applicationSystem.setId(longCount.incrementAndGet());

        // Create the ApplicationSystem
        ApplicationSystemDTO applicationSystemDTO = applicationSystemMapper.toDto(applicationSystem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationSystemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(applicationSystemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationSystem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamApplicationSystem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        applicationSystem.setId(longCount.incrementAndGet());

        // Create the ApplicationSystem
        ApplicationSystemDTO applicationSystemDTO = applicationSystemMapper.toDto(applicationSystem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationSystemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(applicationSystemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ApplicationSystem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateApplicationSystemWithPatch() throws Exception {
        // Initialize the database
        insertedApplicationSystem = applicationSystemRepository.saveAndFlush(applicationSystem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the applicationSystem using partial update
        ApplicationSystem partialUpdatedApplicationSystem = new ApplicationSystem();
        partialUpdatedApplicationSystem.setId(applicationSystem.getId());

        partialUpdatedApplicationSystem.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restApplicationSystemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApplicationSystem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedApplicationSystem))
            )
            .andExpect(status().isOk());

        // Validate the ApplicationSystem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertApplicationSystemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedApplicationSystem, applicationSystem),
            getPersistedApplicationSystem(applicationSystem)
        );
    }

    @Test
    @Transactional
    void fullUpdateApplicationSystemWithPatch() throws Exception {
        // Initialize the database
        insertedApplicationSystem = applicationSystemRepository.saveAndFlush(applicationSystem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the applicationSystem using partial update
        ApplicationSystem partialUpdatedApplicationSystem = new ApplicationSystem();
        partialUpdatedApplicationSystem.setId(applicationSystem.getId());

        partialUpdatedApplicationSystem
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .createdDate(UPDATED_CREATED_DATE)
            .updatedDate(UPDATED_UPDATED_DATE)
            .active(UPDATED_ACTIVE)
            .hashedSecretKey(UPDATED_HASHED_SECRET_KEY);

        restApplicationSystemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedApplicationSystem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedApplicationSystem))
            )
            .andExpect(status().isOk());

        // Validate the ApplicationSystem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertApplicationSystemUpdatableFieldsEquals(
            partialUpdatedApplicationSystem,
            getPersistedApplicationSystem(partialUpdatedApplicationSystem)
        );
    }

    @Test
    @Transactional
    void patchNonExistingApplicationSystem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        applicationSystem.setId(longCount.incrementAndGet());

        // Create the ApplicationSystem
        ApplicationSystemDTO applicationSystemDTO = applicationSystemMapper.toDto(applicationSystem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApplicationSystemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, applicationSystemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(applicationSystemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationSystem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchApplicationSystem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        applicationSystem.setId(longCount.incrementAndGet());

        // Create the ApplicationSystem
        ApplicationSystemDTO applicationSystemDTO = applicationSystemMapper.toDto(applicationSystem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationSystemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(applicationSystemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ApplicationSystem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamApplicationSystem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        applicationSystem.setId(longCount.incrementAndGet());

        // Create the ApplicationSystem
        ApplicationSystemDTO applicationSystemDTO = applicationSystemMapper.toDto(applicationSystem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restApplicationSystemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(applicationSystemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ApplicationSystem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteApplicationSystem() throws Exception {
        // Initialize the database
        insertedApplicationSystem = applicationSystemRepository.saveAndFlush(applicationSystem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the applicationSystem
        restApplicationSystemMockMvc
            .perform(delete(ENTITY_API_URL_ID, applicationSystem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return applicationSystemRepository.count();
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

    protected ApplicationSystem getPersistedApplicationSystem(ApplicationSystem applicationSystem) {
        return applicationSystemRepository.findById(applicationSystem.getId()).orElseThrow();
    }

    protected void assertPersistedApplicationSystemToMatchAllProperties(ApplicationSystem expectedApplicationSystem) {
        assertApplicationSystemAllPropertiesEquals(expectedApplicationSystem, getPersistedApplicationSystem(expectedApplicationSystem));
    }

    protected void assertPersistedApplicationSystemToMatchUpdatableProperties(ApplicationSystem expectedApplicationSystem) {
        assertApplicationSystemAllUpdatablePropertiesEquals(
            expectedApplicationSystem,
            getPersistedApplicationSystem(expectedApplicationSystem)
        );
    }
}
