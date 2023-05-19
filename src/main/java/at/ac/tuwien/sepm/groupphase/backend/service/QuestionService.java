package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.QuestionDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Question;

public interface QuestionService {

    /**
     * Adds a new question to the database.
     *
     * @param question question that is to be added
     * @return question that was added
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.ParsingException is thrown when a parsing error is encountered while testing the logic.
     */
    Question addQuestion(QuestionDto question);

    /**
     * Updates a question by creating a copy with desired changes and incremented version number.
     *
     * @param question question that includes changes
     * @return new version of question
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.ParsingException is thrown when a parsing error is encountered while testing the logic.
     */
    Question updateQuestion(QuestionDto question);

    /**
     * Deletes a question by creating a copy with isDeleted set and incremented version number.
     * Calling this method also removes all question dependsOn entries where the id matches the parameter.
     *
     * @param id id of question that is to be deleted
     */
    void deleteQuestion(Long id);

    /**
     * Returns the question with given id and version number.
     *
     * @return question with given id and version number. Returns NULL if question does not exist.
     */
    Question getSpecificQuestion(Long id, int version);
}
