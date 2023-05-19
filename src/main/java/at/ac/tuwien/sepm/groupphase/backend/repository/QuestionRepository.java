package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Question;
import at.ac.tuwien.sepm.groupphase.backend.entity.IdVersionKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, IdVersionKey> {

    /**
     * Returns all persisted questions with unique id and latest version.
     *
     * @return a list of questions that does not include old versions
     */
    @Query("SELECT q FROM Question q WHERE q.version = (SELECT MAX(q2.version) FROM Question q2 WHERE q2.id = q.id) ORDER BY q.id")
    List<Question> findLatestAll();

    /**
     * Returns the newest version of the question given id.
     *
     * @param id id of returned question
     * @return question with matching id and highest version
     */
    @Query("SELECT q FROM Question q WHERE q.id = ?1 and q.version = (SELECT MAX(q2.version) FROM Question q2 WHERE q2.id = ?1)")
    Question findLatest(Long id);

    /**
     * ID of new questions cannot be set by an annotated generation strategy because it has a composite key.
     * This query acts as a generator for a new id for questions.
     *
     * @return Id value for a new questions
     */
    @Query("SELECT MAX(q.id)+1 FROM Question q")
    Long getNextId();

    /**
     * When a question is flagged as deleted all questions that depend on the deleted one should remove their dependsOn id.
     * This query removes a dependsOn id from all questions.
     */
    @Modifying
    @Transactional
    @Query("UPDATE Question q SET q.dependsOn=null WHERE q.dependsOn = ?1")
    void removeDependsOn(Long id);
}
