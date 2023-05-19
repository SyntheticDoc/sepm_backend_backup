package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ModuleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResultDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Module;
import at.ac.tuwien.sepm.groupphase.backend.entity.Result;

import java.util.List;

public interface ResultService {

    /**
     * Returns all results from the database.
     *
     * @return a result including all the latest questions.
     */
    List<Result> getAllResults();

    /**
     * Returns results of a specific user from the database.
     *
     * @param userPassword password of user for whom results should be returned
     * @return a result including all the latest questions.
     */
    List<Result> getResultsForUser(String userPassword);

    /**
     * Adds a new result to the database.
     *
     * @param resultDto result that is to be added
     * @return result that was added
     */
    Result addResult(ResultDto resultDto);

    /**
     * Deletes the result with id id from the database.
     *
     * @param id id of result that is to be deleted
     */
    void deleteResult(Long id);

    /**
     * Changes result with id id to match the data in resultDto.
     *
     * @param id id of result that is to be changed
     * @param resultDto resultDto to contain all data that needs to be changed
     */
    Result putResult(Long id, ResultDto resultDto);
}
