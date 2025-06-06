package com.esoft.service.mapper;

import com.esoft.domain.ApplicationSystem;
import com.esoft.domain.User;
import com.esoft.service.dto.ApplicationSystemDTO;
import com.esoft.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ApplicationSystem} and its DTO {@link ApplicationSystemDTO}.
 */
@Mapper(componentModel = "spring")
public interface ApplicationSystemMapper extends EntityMapper<ApplicationSystemDTO, ApplicationSystem> {
    @Mapping(target = "users", source = "users", qualifiedByName = "userLoginSet")
    ApplicationSystemDTO toDto(ApplicationSystem s);

    @Mapping(target = "removeUser", ignore = true)
    ApplicationSystem toEntity(ApplicationSystemDTO applicationSystemDTO);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("userLoginSet")
    default Set<UserDTO> toDtoUserLoginSet(Set<User> user) {
        return user.stream().map(this::toDtoUserLogin).collect(Collectors.toSet());
    }
}
