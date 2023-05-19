package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ModuleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.QuestionDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Module;
import at.ac.tuwien.sepm.groupphase.backend.entity.Question;

import java.util.List;

public interface ModuleService {

    /**
     * Returns all module from the database.
     *
     * @return a module including all the latest questions.
     */
    List<Module> getAllModules();

    /**
     * Adds a new module to the database.
     *
     * @param moduleDto module that is to be added
     * @return module that was added
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.ParsingException is thrown when a parsing error is encountered while testing the logic.
     */
    Module addModule(ModuleDto moduleDto);

    /**
     * Deletes a module by setting the deleted value to true.
     *
     * @param id id of module that is to be deleted
     */
    void deleteModule(Long id);

    /**
     * Adds a new question to the module.
     *
     * @param question question that is to be added
     * @return question that was added
     */
    Module addQuestion(Long id, Long question);

    /**
     * Changes Module with id id to match the data in questionDto.
     *
     * @param id id of module that is to be changed
     * @param moduleDto moduleDto to contain all data that needs to be changed
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.ParsingException is thrown when a parsing error is encountered while testing the logic.
     */
    Module putModule(Long id, ModuleDto moduleDto);

    /**
     * Deletes the question with id questionId saved in module with id id.
     *
     * @param id id of module that is to be deleted
     * @param questionId id of the question to delete
     */
    void deleteQuestion(Long id, Long questionId);

    /**
     * Gets a module with the given id.
     *
     * @param id of the module
     */
    Module getById(Long id);
}
