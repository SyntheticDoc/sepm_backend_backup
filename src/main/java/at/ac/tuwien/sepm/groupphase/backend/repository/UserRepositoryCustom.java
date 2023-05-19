package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserFindDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Custom user repo that handles more compilcated user queries.
 */
public interface UserRepositoryCustom {

    /**
     * Finds all users matching the given criteria.
     *
     * @see UserFindDto
     * @param findDto criteria
     * @param or true to match any user with one of the given values
     * @return list of application users
     */
    List<ApplicationUser> findUsers(UserFindDto findDto, boolean or);

}
