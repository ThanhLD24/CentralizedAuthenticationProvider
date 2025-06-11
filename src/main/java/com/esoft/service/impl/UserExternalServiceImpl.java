package com.esoft.service.impl;

import com.esoft.domain.User;
import com.esoft.service.errors.IdNotEmptyException;
import com.esoft.service.errors.LoginAlreadyUsedException;
import com.esoft.service.errors.UserNotFoundException;
import com.esoft.repository.UserRepository;
import com.esoft.service.UserExternalService;
import com.esoft.service.UserInternalService;
import com.esoft.service.dto.AdminUserDTO;
import com.esoft.service.mapper.UserMapper;
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
        Optional<User> existingUserOpt = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase());
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.orElseThrow();
            if (!existingUser.getId().equals(userDTO.getId())) {
                throw new LoginAlreadyUsedException();
            }
        }
        return userInternalService.updateUser(userDTO).orElseThrow();
    }

    @Override
    public void deleteUser(String username) {
        userRepository
            .findOneByLogin(username)
            .map(user -> {
                userRepository.delete(user);
                return user;
            })
            .orElseThrow(() -> new UserNotFoundException(username));
    }

    @Override
    public AdminUserDTO findUserByUsername(String username) {
        Optional<User> user = userRepository.findOneWithAuthoritiesByLogin(username);
        if (user.isEmpty())
            throw new UserNotFoundException(username);
        return mapper.userToAdminUserDTO(user.orElseThrow());
    }

}
