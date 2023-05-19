package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Argon2Parameters;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface Argon2ParametersRepository extends JpaRepository<Argon2Parameters, Long> {
    /**
     * Returns a Argon2Parameters-Entity containing the parameters of the type specified as parameter.
     *
     * @param type string describing the parameters to get
     * @return Argon2Parameters containing the parameters of the type specified
     */
    @Query("SELECT p FROM Argon2Parameters p WHERE p.type = ?1")
    Argon2Parameters findByType(String type);
}