package com.esoft.service.impl;

import com.esoft.domain.ApplicationSystem;
import com.esoft.repository.ApplicationSystemRepository;
import com.esoft.service.ApplicationSystemService;
import com.esoft.service.dto.ApplicationSystemDTO;
import com.esoft.service.mapper.ApplicationSystemMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public ApplicationSystemServiceImpl(
        ApplicationSystemRepository applicationSystemRepository,
        ApplicationSystemMapper applicationSystemMapper
    ) {
        this.applicationSystemRepository = applicationSystemRepository;
        this.applicationSystemMapper = applicationSystemMapper;
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

    public Page<ApplicationSystemDTO> findAllWithEagerRelationships(Pageable pageable) {
        return applicationSystemRepository.findAllWithEagerRelationships(pageable).map(applicationSystemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApplicationSystemDTO> findOne(Long id) {
        LOG.debug("Request to get ApplicationSystem : {}", id);
        return applicationSystemRepository.findOneWithEagerRelationships(id).map(applicationSystemMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ApplicationSystem : {}", id);
        applicationSystemRepository.deleteById(id);
    }
}
