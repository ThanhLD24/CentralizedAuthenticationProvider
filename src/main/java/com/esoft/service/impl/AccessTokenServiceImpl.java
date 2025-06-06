package com.esoft.service.impl;

import com.esoft.domain.AccessToken;
import com.esoft.repository.AccessTokenRepository;
import com.esoft.service.AccessTokenService;
import com.esoft.service.dto.AccessTokenDTO;
import com.esoft.service.mapper.AccessTokenMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.esoft.domain.AccessToken}.
 */
@Service
@Transactional
public class AccessTokenServiceImpl implements AccessTokenService {

    private static final Logger LOG = LoggerFactory.getLogger(AccessTokenServiceImpl.class);

    private final AccessTokenRepository accessTokenRepository;

    private final AccessTokenMapper accessTokenMapper;

    public AccessTokenServiceImpl(AccessTokenRepository accessTokenRepository, AccessTokenMapper accessTokenMapper) {
        this.accessTokenRepository = accessTokenRepository;
        this.accessTokenMapper = accessTokenMapper;
    }

    @Override
    public AccessTokenDTO save(AccessTokenDTO accessTokenDTO) {
        LOG.debug("Request to save AccessToken : {}", accessTokenDTO);
        AccessToken accessToken = accessTokenMapper.toEntity(accessTokenDTO);
        accessToken = accessTokenRepository.save(accessToken);
        return accessTokenMapper.toDto(accessToken);
    }

    @Override
    public AccessTokenDTO update(AccessTokenDTO accessTokenDTO) {
        LOG.debug("Request to update AccessToken : {}", accessTokenDTO);
        AccessToken accessToken = accessTokenMapper.toEntity(accessTokenDTO);
        accessToken = accessTokenRepository.save(accessToken);
        return accessTokenMapper.toDto(accessToken);
    }

    @Override
    public Optional<AccessTokenDTO> partialUpdate(AccessTokenDTO accessTokenDTO) {
        LOG.debug("Request to partially update AccessToken : {}", accessTokenDTO);

        return accessTokenRepository
            .findById(accessTokenDTO.getId())
            .map(existingAccessToken -> {
                accessTokenMapper.partialUpdate(existingAccessToken, accessTokenDTO);

                return existingAccessToken;
            })
            .map(accessTokenRepository::save)
            .map(accessTokenMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccessTokenDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all AccessTokens");
        return accessTokenRepository.findAll(pageable).map(accessTokenMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AccessTokenDTO> findOne(Long id) {
        LOG.debug("Request to get AccessToken : {}", id);
        return accessTokenRepository.findById(id).map(accessTokenMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete AccessToken : {}", id);
        accessTokenRepository.deleteById(id);
    }
}
