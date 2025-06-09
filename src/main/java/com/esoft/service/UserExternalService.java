package com.esoft.service;

import com.esoft.service.dto.AdminUserDTO;

public interface UserExternalService {
    AdminUserDTO createUser(AdminUserDTO dto);
    AdminUserDTO updateUser(AdminUserDTO dto);
    void deleteUser(String username);
    AdminUserDTO findUserByUsername(String username);

}
