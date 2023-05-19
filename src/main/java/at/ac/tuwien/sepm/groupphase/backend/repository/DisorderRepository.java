package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Disorder;
import at.ac.tuwien.sepm.groupphase.backend.entity.IdVersionKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisorderRepository extends JpaRepository<Disorder, IdVersionKey> {

    /**
     * Returns all persisted disorders with unique id and latest version.
     *
     * @return a list of disorders that does not include old versions of same disorder
     */
    @Query("SELECT d FROM Disorder d WHERE d.version = (SELECT MAX(d2.version) FROM Disorder d2 WHERE d2.id = d.id)")
    List<Disorder> findLatestAll();

    /**
     * Returns the newest version of the disorder with given id.
     *
     * @param id id of the disorder to search for
     * @return disorder with given id and highest version
     */
    @Query("SELECT d FROM Disorder d WHERE d.id = ?1 and d.version = (SELECT MAX(d2.version) FROM Disorder d2 WHERE d2.id = ?1)")
    Disorder findLatest(Long id);

    /**
     * generates a new Id value for a new disorder.
     *
     * @return Id value for a new disorder
     */
    @Query("SELECT MAX(d.id)+1 FROM Disorder d")
    Long getNextId();
}
