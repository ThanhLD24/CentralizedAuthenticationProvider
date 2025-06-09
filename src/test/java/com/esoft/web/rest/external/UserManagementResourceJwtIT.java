package com.esoft.web.rest.external;

import com.esoft.IntegrationTest;
import com.esoft.domain.Authority;
import com.esoft.domain.User;
import com.esoft.repository.UserRepository;
import com.esoft.service.dto.AdminUserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
@ActiveProfiles("test")
class UserManagementResourceJwtIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    void setup() throws Exception {
        // Tạo user admin nếu chưa có
        if (userRepository.findOneByLogin("admin").isEmpty()) {
            User admin = new User();
            admin.setLogin("admin");
            admin.setPassword("{bcrypt}$2a$10$7mTPx...");
            admin.setActivated(true);
            admin.setEmail("admin@example.com");
            admin.setFirstName("Admin");
            admin.setLastName("Test");
            admin.setAuthorities(Set.of(new Authority("ROLE_ADMIN")));
            userRepository.saveAndFlush(admin);
        }

        // Lấy JWT token
        String loginPayload = """
            {
              "username": "admin",
              "password": "admin"
            }
            """;

        var result = mockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginPayload))
            .andExpect(status().isOk())
            .andReturn();

        String body = result.getResponse().getContentAsString();
        jwtToken = objectMapper.readTree(body).get("id_token").asText();
    }

    @Test
    void createUser_withJwt_shouldSucceed() throws Exception {
        AdminUserDTO dto = new AdminUserDTO();
        dto.setLogin("userjwt");
        dto.setPassword("pass");

        mockMvc.perform(post("/api/user/create")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message", is("User created successfully")));

        assertThat(userRepository.findOneByLogin("userjwt")).isPresent();
    }

    @Test
    void getUser_withJwt_shouldReturnUser() throws Exception {
        // Setup
        User user = new User();
        user.setLogin("jwtuser");
        user.setPassword("pass");
        userRepository.saveAndFlush(user);

        mockMvc.perform(get("/api/user/get/jwtuser")
                .header("Authorization", "Bearer " + jwtToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.login").value("jwtuser"));
    }

    @Test
    void deleteUser_withJwt_shouldSucceed() throws Exception {
        User user = new User();
        user.setLogin("todelete");
        user.setPassword("xxx");
        userRepository.saveAndFlush(user);

        mockMvc.perform(delete("/api/user/delete/todelete")
                .header("Authorization", "Bearer " + jwtToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("User deleted successfully"));

        assertThat(userRepository.findOneByLogin("todelete")).isEmpty();
    }

    @Test
    void callWithoutJwt_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/user/get/someone"))
            .andExpect(status().isUnauthorized());
    }
}
