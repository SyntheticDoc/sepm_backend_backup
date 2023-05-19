package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.IdVersionKey;
import org.springframework.data.jpa.repository.JpaRepository;
import at.ac.tuwien.sepm.groupphase.backend.entity.Module;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, IdVersionKey> {

    /**
     * Returns all persisted modules with unique id and latest version.
     *
     * @return a list of modules that does not include old versions
     */
    @Query("SELECT m FROM Module m WHERE m.version = (SELECT MAX(m2.version) FROM Module m2 WHERE m2.id = m.id)")
    List<Module> findLatestAll();

    /**
     * ID of new modules cannot be set by an annotated generation strategy because it has a composite key.
     * This query acts as a generator for a new id for modules.
     *
     * @return Id value for a new modules
     */
    @Query("SELECT MAX(m.id)+1 FROM Module m")
    Long getNextId();

    /**
     * Returns the newest version of the module given id.
     *
     * @param id id of returned module
     * @return module with matching id and highest version
     */
    @Query("SELECT m FROM Module m WHERE m.id = ?1 and m.version = (SELECT MAX(m2.version) FROM Module m2 WHERE m2.id = ?1)")
    Module findLatest(Long id);
}
