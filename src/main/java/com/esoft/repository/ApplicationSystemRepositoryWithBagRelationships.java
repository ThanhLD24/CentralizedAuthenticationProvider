package com.esoft.repository;

import com.esoft.domain.ApplicationSystem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface ApplicationSystemRepositoryWithBagRelationships {
    Optional<ApplicationSystem> fetchBagRelationships(Optional<ApplicationSystem> applicationSystem);

    List<ApplicationSystem> fetchBagRelationships(List<ApplicationSystem> applicationSystems);

    Page<ApplicationSystem> fetchBagRelationships(Page<ApplicationSystem> applicationSystems);
}
