package com.esoft.web.rest.external;

import com.esoft.CentralizedAuthenticationProviderApp;
import com.esoft.IntegrationTest;
import com.esoft.domain.Transaction;
import com.esoft.domain.User;
import com.esoft.repository.TransactionRepository;
import com.esoft.repository.UserRepository;
import com.esoft.service.dto.TokenResponseDTO;
import com.esoft.service.mapper.EntityMapper;
import com.esoft.web.filter.TransactionLogFilter;
import com.esoft.web.rest.dto.ApiResponse;
import com.esoft.web.rest.dto.AuthRequest;
import com.esoft.web.rest.dto.RefreshTokenRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
////@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
@ActiveProfiles("test")
public class AuthenticationResourceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionLogFilter transactionLogFilter;

    @BeforeEach
    @Transactional
    public void setUp() {
        userRepository.deleteAll();

        User admin = new User();
        admin.setLogin("admin");
        admin.setPassword("$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC");
        admin.setFirstName("Administrator");
        admin.setLastName("Administrator");
        admin.setEmail("admin@localhost");
        admin.setActivated(true);
        admin.setLangKey("en");
        admin.setCreatedBy("system");
        admin.setLastModifiedBy("system");

        User user = new User();
        user.setLogin("user");
        user.setPassword("$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K");
        user.setFirstName("User");
        user.setLastName("User");
        user.setEmail("user@localhost");
        user.setActivated(true);
        user.setLangKey("en");
        user.setCreatedBy("system");
        user.setLastModifiedBy("system");

        userRepository.save(admin);
        userRepository.save(user);
    }


    @Test
    void testCreateToken() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername("admin");
        request.setPassword("admin");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/create-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn();

        String json = result.getResponse().getContentAsString();
        ApiResponse<TokenResponseDTO> response = objectMapper.readValue(json,
            objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, TokenResponseDTO.class));

        assertThat(response.getData().getAccessToken()).isNotEmpty();
        assertThat(response.getData().getRefreshToken()).isNotEmpty();
    }

    @Test
    void testRefreshToken() throws Exception {
        // Đầu tiên login để lấy refresh token
        AuthRequest request = new AuthRequest();
        request.setUsername("admin");
        request.setPassword("admin");

        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/create-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn();

        String loginJson = loginResult.getResponse().getContentAsString();
        ApiResponse<TokenResponseDTO> loginResponse = objectMapper.readValue(loginJson,
            objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, TokenResponseDTO.class));

        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
        refreshRequest.setRefreshToken(loginResponse.getData().getRefreshToken());

        MvcResult refreshResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
            .andExpect(status().isOk())
            .andReturn();

        String refreshJson = refreshResult.getResponse().getContentAsString();
        ApiResponse<TokenResponseDTO> refreshResponse = objectMapper.readValue(refreshJson,
            objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, TokenResponseDTO.class));

        assertThat(refreshResponse.getData().getAccessToken()).isNotEmpty();
        assertThat(refreshResponse.getData().getRefreshToken()).isNotEmpty();
    }

    @Test
    void testValidateToken() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername("admin");
        request.setPassword("admin");

        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/create-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn();

        String loginJson = loginResult.getResponse().getContentAsString();
        ApiResponse<TokenResponseDTO> loginResponse = objectMapper.readValue(loginJson,
            objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, TokenResponseDTO.class));

        String token = loginResponse.getData().getAccessToken();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/validate-token")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    void testDisableToken() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername("admin");
        request.setPassword("admin");

        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/create-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn();

        String loginJson = loginResult.getResponse().getContentAsString();
        ApiResponse<TokenResponseDTO> loginResponse = objectMapper.readValue(loginJson,
            objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, TokenResponseDTO.class));

        String token = loginResponse.getData().getAccessToken();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/disable-token")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }
}
