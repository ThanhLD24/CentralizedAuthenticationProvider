package com.esoft.web.rest.external;

import com.esoft.IntegrationTest;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
@ActiveProfiles("test")
class UserManagementResourceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void createUser_shouldReturnSuccess() throws Exception {
        AdminUserDTO userDTO = new AdminUserDTO();
        userDTO.setLogin("admin");
        userDTO.setPassword("pass123");

        mockMvc.perform(post("/api/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("SUCCESS")))
            .andExpect(jsonPath("$.message", is("User created successfully")))
            .andExpect(jsonPath("$.data.login", is("admin")));

        Optional<User> saved = userRepository.findOneByLogin("admin");
        assertThat(saved).isPresent();
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void updateUser_shouldReturnSuccess() throws Exception {
        // Setup user
        User user = new User();
        user.setLogin("admin");
        user.setPassword("$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K");
        user = userRepository.save(user);

        AdminUserDTO updatedDTO = new AdminUserDTO();
        updatedDTO.setId(user.getId());
        updatedDTO.setLogin("admin");

        mockMvc.perform(put("/api/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("User updated successfully"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getUser_shouldReturnUser() throws Exception {
        // Setup user
        User user = new User();
        user.setLogin("testuser");
        user.setPassword("123456");
        userRepository.save(user);

        mockMvc.perform(get("/api/user/get/testuser"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.message").value("Found user by username"))
            .andExpect(jsonPath("$.data.login").value("testuser"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void deleteUser_shouldSucceed() throws Exception {
        // Setup user
        User user = new User();
        user.setLogin("delete-me");
        user.setPassword("pass");
        userRepository.save(user);

        mockMvc.perform(delete("/api/user/delete/delete-me"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("User deleted successfully"));

        Optional<User> deleted = userRepository.findOneByLogin("delete-me");
        assertThat(deleted).isEmpty();
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void createUser_shouldThrow_whenIdIsNotNull() throws Exception {
        AdminUserDTO userDTO = new AdminUserDTO();
        userDTO.setId(99L); // ID không được có
        userDTO.setLogin("someone");

        mockMvc.perform(post("/api/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
            .andExpect(status().isBadRequest());
    }
}
