package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Question;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SecurityDataRepository extends JpaRepository<SecurityData, Long> {
    /**
     * Returns a SecurityData-Entity containing the value of the type specified as parameter.
     *
     * @param type string describing the value to get
     * @return SecurityData containing the value of the type specified as parameter
     */
    @Query("SELECT s FROM SecurityData s WHERE s.type = ?1")
    SecurityData findByType(String type);
}