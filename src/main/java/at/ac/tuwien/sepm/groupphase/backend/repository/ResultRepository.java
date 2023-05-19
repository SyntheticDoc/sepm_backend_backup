package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {
    /**
     * Returns a list of questionnaire results for a single user.
     *
     * @param userHash string specific for the user for whom to get the list
     * @return List of Result-Objects containing the results for the user as a list
     */
    @Query("SELECT r FROM Result r WHERE r.userHash = ?1")
    List<Result> findResultsForUser(String userHash);
}
