package at.ac.tuwien.sepm.groupphase.backend.repository.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserFindDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class UserRepositoryImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ApplicationUser> findUsers(UserFindDto findDto, boolean or) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ApplicationUser> query = cb.createQuery(ApplicationUser.class);
        Root<ApplicationUser> root = query.from(ApplicationUser.class);

        Predicate usernamePred = cb.equal(root.get("username"), findDto.username());
        Predicate emailPred = cb.equal(root.get("email"), findDto.email());
        Predicate combinedPred;
        if (or) {
            combinedPred = cb.or(usernamePred, emailPred);
        } else {
            combinedPred = cb.and(usernamePred, emailPred);
        }
        query.where(combinedPred);

        return entityManager.createQuery(query).getResultList();
    }
}
