package com.esoft.service.mapper;

import com.esoft.domain.Transaction;
import com.esoft.domain.User;
import com.esoft.service.dto.TransactionDTO;
import com.esoft.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Transaction} and its DTO {@link TransactionDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransactionMapper extends EntityMapper<TransactionDTO, Transaction> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    TransactionDTO toDto(Transaction s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
