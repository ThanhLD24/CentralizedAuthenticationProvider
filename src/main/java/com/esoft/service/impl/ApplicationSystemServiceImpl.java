package com.esoft.service.impl;

import com.esoft.domain.ApplicationSystem;
import com.esoft.repository.ApplicationSystemRepository;
import com.esoft.service.ApplicationSystemService;
import com.esoft.service.dto.ApplicationSystemDTO;
import com.esoft.service.mapper.ApplicationSystemMapper;
import java.util.Optional;

import com.esoft.utils.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.esoft.domain.ApplicationSystem}.
 */
@Service
@Transactional
public class ApplicationSystemServiceImpl implements ApplicationSystemService {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationSystemServiceImpl.class);

    private final ApplicationSystemRepository applicationSystemRepository;

    private final ApplicationSystemMapper applicationSystemMapper;
    private final JWTUtil jwtUtil;

    public ApplicationSystemServiceImpl(
            ApplicationSystemRepository applicationSystemRepository,
            ApplicationSystemMapper applicationSystemMapper, JWTUtil jwtUtil
    ) {
        this.applicationSystemRepository = applicationSystemRepository;
        this.applicationSystemMapper = applicationSystemMapper;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public ApplicationSystemDTO save(ApplicationSystemDTO applicationSystemDTO) {
        LOG.debug("Request to save ApplicationSystem : {}", applicationSystemDTO);
        ApplicationSystem applicationSystem = applicationSystemMapper.toEntity(applicationSystemDTO);
        applicationSystem = applicationSystemRepository.save(applicationSystem);
        return applicationSystemMapper.toDto(applicationSystem);
    }

    @Override
    public ApplicationSystemDTO update(ApplicationSystemDTO applicationSystemDTO) {
        LOG.debug("Request to update ApplicationSystem : {}", applicationSystemDTO);
        ApplicationSystem applicationSystem = applicationSystemMapper.toEntity(applicationSystemDTO);
        applicationSystem = applicationSystemRepository.save(applicationSystem);
        return applicationSystemMapper.toDto(applicationSystem);
    }

    @Override
    public Optional<ApplicationSystemDTO> partialUpdate(ApplicationSystemDTO applicationSystemDTO) {
        LOG.debug("Request to partially update ApplicationSystem : {}", applicationSystemDTO);

        return applicationSystemRepository
            .findById(applicationSystemDTO.getId())
            .map(existingApplicationSystem -> {
                applicationSystemMapper.partialUpdate(existingApplicationSystem, applicationSystemDTO);

                return existingApplicationSystem;
            })
            .map(applicationSystemRepository::save)
            .map(applicationSystemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationSystemDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ApplicationSystems");
        return applicationSystemRepository.findAll(pageable).map(applicationSystemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApplicationSystemDTO> findOne(Long id) {
        LOG.debug("Request to get ApplicationSystem : {}", id);
        return applicationSystemRepository.findById(id).map(applicationSystemMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ApplicationSystem : {}", id);
        applicationSystemRepository.deleteById(id);
    }


    @Override
    public Optional<ApplicationSystemDTO> findBySecretKeyAndActive(String secretKey, boolean active) {
        String hashedSecretKey = jwtUtil.hashToken(secretKey);
        return applicationSystemRepository.findByHashedSecretKeyAndActive(hashedSecretKey, active)
            .map(applicationSystemMapper::toDto);
    }
}
