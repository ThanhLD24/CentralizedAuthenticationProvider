package com.esoft.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.esoft.domain.User;
import com.esoft.repository.UserRepository;
import com.esoft.service.UserInternalService;
import com.esoft.service.dto.AdminUserDTO;
import com.esoft.service.mapper.UserMapper;
import com.esoft.service.errors.IdNotEmptyException;
import com.esoft.service.errors.LoginAlreadyUsedException;
import com.esoft.service.errors.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserExternalServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserInternalService userInternalService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserExternalServiceImpl userExternalService;


    @Test
    void createUser_shouldThrow_whenIdIsNotNull() {
        AdminUserDTO dto = new AdminUserDTO();
        dto.setId(1L);
        assertThrows(IdNotEmptyException.class, () -> userExternalService.createUser(dto));
    }

    @Test
    void createUser_shouldThrow_whenLoginAlreadyUsed() {
        AdminUserDTO dto = new AdminUserDTO();
        dto.setLogin("admin");
        when(userRepository.findOneByLogin("admin")).thenReturn(Optional.of(new User()));

        assertThrows(LoginAlreadyUsedException.class, () -> userExternalService.createUser(dto));
    }

    @Test
    void createUser_shouldReturnDTO_whenValid() {
        AdminUserDTO dto = new AdminUserDTO();
        dto.setLogin("admin");
        dto.setPassword("pass");

        User user = new User();
        AdminUserDTO expected = new AdminUserDTO();

        when(userRepository.findOneByLogin("admin")).thenReturn(Optional.empty());
        when(userInternalService.registerUser(dto, "pass")).thenReturn(user);
        when(userMapper.userToAdminUserDTO(user)).thenReturn(expected);

        AdminUserDTO result = userExternalService.createUser(dto);
        assertEquals(expected, result);
    }

    @Test
    void updateUser_shouldThrow_whenLoginUsedByAnotherUser() {
        AdminUserDTO dto = new AdminUserDTO();
        dto.setId(1L);
        dto.setLogin("Admin");

        User existing = new User();
        existing.setId(2L);
        existing.setLogin("admin");

        when(userRepository.findOneByLogin("admin")).thenReturn(Optional.of(existing));

        assertThrows(LoginAlreadyUsedException.class, () -> userExternalService.updateUser(dto));
        verify(userInternalService, never()).updateUser(any());
    }

    @Test
    void updateUser_shouldReturnUpdatedDTO_whenValid() {
        AdminUserDTO dto = new AdminUserDTO();
        dto.setId(1L);
        dto.setLogin("admin");

        User user = new User();
        user.setId(1L);

        AdminUserDTO updated = new AdminUserDTO();

        when(userRepository.findOneByLogin("admin")).thenReturn(Optional.of(user));
        when(userInternalService.updateUser(dto)).thenReturn(Optional.of(updated));

        AdminUserDTO result = userExternalService.updateUser(dto);
        assertEquals(updated, result);
    }

    @Test
    void findUserByUsername_shouldThrow_whenNotFound() {
        when(userRepository.findOneWithAuthoritiesByLogin("admin")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userExternalService.findUserByUsername("admin"));
    }

    @Test
    void findUserByUsername_shouldReturnDTO_whenFound() {
        User user = new User();
        AdminUserDTO dto = new AdminUserDTO();

        when(userRepository.findOneWithAuthoritiesByLogin("admin")).thenReturn(Optional.of(user));
        when(userMapper.userToAdminUserDTO(user)).thenReturn(dto);

        AdminUserDTO result = userExternalService.findUserByUsername("admin");
        assertEquals(dto, result);
    }
}
