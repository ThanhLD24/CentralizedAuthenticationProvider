package com.esoft.service.impl;

import com.esoft.domain.User;
import com.esoft.repository.UserRepository;
import com.esoft.service.UserExternalService;
import com.esoft.service.UserInternalService;
import com.esoft.service.dto.AdminUserDTO;
import com.esoft.service.mapper.UserMapper;
import com.esoft.web.rest.errors.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserExternalServiceImpl implements UserExternalService {
    private final UserRepository userRepository;
    private final UserInternalService userInternalService;
    private final UserMapper mapper;
    public UserExternalServiceImpl(UserRepository userRepository, UserInternalService userInternalService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userInternalService = userInternalService;
        this.mapper = userMapper;
    }
    @Override
    @Transactional
    public AdminUserDTO createUser(AdminUserDTO userDTO) {
        if (userDTO.getId() != null) {
            throw new IdNotEmptyException();
        } else if (userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).isPresent()) {
            throw new LoginAlreadyUsedException();
        } else {
            User createdUser = userInternalService.registerUser(userDTO, userDTO.getPassword());
            return mapper.userToAdminUserDTO(createdUser);
        }
    }

    @Override
    public AdminUserDTO updateUser(AdminUserDTO userDTO) {
        Optional<User> existingUser = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.orElseThrow().getId().equals(userDTO.getId()))) {
            throw new LoginAlreadyUsedException();
        }
        return userInternalService.updateUser(userDTO).get();
    }

    @Override
    public void deleteUser(String username) {
        userInternalService.deleteUser(username);
    }

    @Override
    public AdminUserDTO findUserByUsername(String username) {
        Optional<User> user = userRepository.findOneWithAuthoritiesByLogin(username);
        if (!user.isPresent())
            throw new UserNotFoundException(username);
        return mapper.userToAdminUserDTO(user.get());
    }

}
