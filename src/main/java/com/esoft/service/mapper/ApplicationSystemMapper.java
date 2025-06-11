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

}
