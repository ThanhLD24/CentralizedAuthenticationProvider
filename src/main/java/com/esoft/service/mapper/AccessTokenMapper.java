package com.esoft.service.mapper;

import com.esoft.domain.AccessToken;
import com.esoft.service.dto.AccessTokenDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AccessToken} and its DTO {@link AccessTokenDTO}.
 */
@Mapper(componentModel = "spring")
public interface AccessTokenMapper extends EntityMapper<AccessTokenDTO, AccessToken> {}
