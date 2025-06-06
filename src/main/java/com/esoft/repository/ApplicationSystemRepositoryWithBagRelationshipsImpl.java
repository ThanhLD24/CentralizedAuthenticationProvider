package com.esoft.repository;

import com.esoft.domain.ApplicationSystem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class ApplicationSystemRepositoryWithBagRelationshipsImpl implements ApplicationSystemRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String APPLICATIONSYSTEMS_PARAMETER = "applicationSystems";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<ApplicationSystem> fetchBagRelationships(Optional<ApplicationSystem> applicationSystem) {
        return applicationSystem.map(this::fetchUsers);
    }

    @Override
    public Page<ApplicationSystem> fetchBagRelationships(Page<ApplicationSystem> applicationSystems) {
        return new PageImpl<>(
            fetchBagRelationships(applicationSystems.getContent()),
            applicationSystems.getPageable(),
            applicationSystems.getTotalElements()
        );
    }

    @Override
    public List<ApplicationSystem> fetchBagRelationships(List<ApplicationSystem> applicationSystems) {
        return Optional.of(applicationSystems).map(this::fetchUsers).orElse(Collections.emptyList());
    }

    ApplicationSystem fetchUsers(ApplicationSystem result) {
        return entityManager
            .createQuery(
                "select applicationSystem from ApplicationSystem applicationSystem left join fetch applicationSystem.users where applicationSystem.id = :id",
                ApplicationSystem.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<ApplicationSystem> fetchUsers(List<ApplicationSystem> applicationSystems) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, applicationSystems.size()).forEach(index -> order.put(applicationSystems.get(index).getId(), index));
        List<ApplicationSystem> result = entityManager
            .createQuery(
                "select applicationSystem from ApplicationSystem applicationSystem left join fetch applicationSystem.users where applicationSystem in :applicationSystems",
                ApplicationSystem.class
            )
            .setParameter(APPLICATIONSYSTEMS_PARAMETER, applicationSystems)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
