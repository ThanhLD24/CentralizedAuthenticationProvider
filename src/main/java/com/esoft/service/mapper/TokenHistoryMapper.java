package com.esoft.service.mapper;

import com.esoft.domain.TokenHistory;
import com.esoft.service.dto.TokenHistoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TokenHistory} and its DTO {@link TokenHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface TokenHistoryMapper extends EntityMapper<TokenHistoryDTO, TokenHistory> {}
