package com.esoft.service.impl;

import com.esoft.domain.TokenHistory;
import com.esoft.repository.TokenHistoryRepository;
import com.esoft.service.TokenHistoryService;
import com.esoft.service.dto.TokenHistoryDTO;
import com.esoft.service.mapper.TokenHistoryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.esoft.domain.TokenHistory}.
 */
@Service
@Transactional
public class TokenHistoryServiceImpl implements TokenHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(TokenHistoryServiceImpl.class);

    private final TokenHistoryRepository tokenHistoryRepository;

    private final TokenHistoryMapper tokenHistoryMapper;

    public TokenHistoryServiceImpl(TokenHistoryRepository tokenHistoryRepository, TokenHistoryMapper tokenHistoryMapper) {
        this.tokenHistoryRepository = tokenHistoryRepository;
        this.tokenHistoryMapper = tokenHistoryMapper;
    }

    @Override
    public TokenHistoryDTO save(TokenHistoryDTO tokenHistoryDTO) {
        LOG.debug("Request to save TokenHistory : {}", tokenHistoryDTO);
        TokenHistory tokenHistory = tokenHistoryMapper.toEntity(tokenHistoryDTO);
        tokenHistory = tokenHistoryRepository.save(tokenHistory);
        return tokenHistoryMapper.toDto(tokenHistory);
    }

    @Override
    public TokenHistoryDTO update(TokenHistoryDTO tokenHistoryDTO) {
        LOG.debug("Request to update TokenHistory : {}", tokenHistoryDTO);
        TokenHistory tokenHistory = tokenHistoryMapper.toEntity(tokenHistoryDTO);
        tokenHistory = tokenHistoryRepository.save(tokenHistory);
        return tokenHistoryMapper.toDto(tokenHistory);
    }

    @Override
    public Optional<TokenHistoryDTO> partialUpdate(TokenHistoryDTO tokenHistoryDTO) {
        LOG.debug("Request to partially update TokenHistory : {}", tokenHistoryDTO);

        return tokenHistoryRepository
            .findById(tokenHistoryDTO.getId())
            .map(existingTokenHistory -> {
                tokenHistoryMapper.partialUpdate(existingTokenHistory, tokenHistoryDTO);

                return existingTokenHistory;
            })
            .map(tokenHistoryRepository::save)
            .map(tokenHistoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TokenHistoryDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all TokenHistorys");
        return tokenHistoryRepository.findAll(pageable).map(tokenHistoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TokenHistoryDTO> findOne(Long id) {
        LOG.debug("Request to get TokenHistory : {}", id);
        return tokenHistoryRepository.findById(id).map(tokenHistoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete TokenHistory : {}", id);
        tokenHistoryRepository.deleteById(id);
    }
}
