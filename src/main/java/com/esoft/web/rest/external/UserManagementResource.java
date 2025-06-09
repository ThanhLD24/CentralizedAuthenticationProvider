package com.esoft.web.rest.external;

import com.esoft.config.Constants;
import com.esoft.domain.User;
import com.esoft.security.AuthoritiesConstants;
import com.esoft.service.UserExternalService;
import com.esoft.service.UserInternalService;
import com.esoft.service.dto.AdminUserDTO;
import com.esoft.service.mapper.UserMapper;
import com.esoft.web.rest.dto.ApiResponse;
import com.esoft.web.rest.dto.ResponseStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
public class UserManagementResource {
    private static final Logger LOG = LoggerFactory.getLogger(UserManagementResource.class);
    private final UserExternalService userExternalService;
    public UserManagementResource(UserExternalService userExternalService) {
        this.userExternalService = userExternalService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<AdminUserDTO>> createUser(@RequestBody AdminUserDTO userDTO) {
        LOG.info("Creating user: {}", userDTO.getLogin());
        AdminUserDTO user = userExternalService.createUser(userDTO);
        return ResponseEntity.ok(
            ApiResponse.<AdminUserDTO>builder()
                .status(ResponseStatus.SUCCESS)
                .message("User created successfully")
                .data(user)
                .build()
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<AdminUserDTO>> updateUser(
        @Valid @RequestBody AdminUserDTO userDTO
    ) {
        LOG.info("Update user: {}", userDTO.getLogin());
        AdminUserDTO user = userExternalService.updateUser(userDTO);
        return ResponseEntity.ok(
            ApiResponse.<AdminUserDTO>builder()
                .status(ResponseStatus.SUCCESS)
                .message("User updated successfully")
                .data(user)
                .build()
        );
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String username) {
        LOG.info("Deleting user: {}", username);
        userExternalService.deleteUser(username);
        return ResponseEntity.ok( ApiResponse.<Void>builder()
            .status(ResponseStatus.SUCCESS)
            .message("User deleted successfully")
            .build());
    }

    @GetMapping("/get/{username}")
    public ResponseEntity<ApiResponse<AdminUserDTO>> getUser(@PathVariable String username) {
        AdminUserDTO result = userExternalService.findUserByUsername(username);
        return ResponseEntity.ok(
            ApiResponse.<AdminUserDTO>builder()
                .status(ResponseStatus.SUCCESS)
                .message("Found user by username")
                .data(result)
                .build()
        );
    }

}
