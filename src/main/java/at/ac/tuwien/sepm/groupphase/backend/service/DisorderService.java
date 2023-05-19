package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DisorderDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Disorder;


import java.util.List;


public interface DisorderService {

    /**
     * Returns all disorders from the database.
     *
     * @return a list of the latest versions of all disorders
     */
    List<Disorder> getAllDisorders();

    /**
     * Adds a new disorder to the database.
     *
     * @param disorder disorder to be added to the database
     * @return disorder that was added
     */
    Disorder addDisorder(DisorderDto disorder);

    /**
     * Updates an already existing disorder by creating a copy with desired changes and incremented version number.
     *
     * @param disorder disorder that includes changes
     * @return new version of the disorder
     */
    Disorder updateDisorder(DisorderDto disorder);

    /**
     * Deletes a question by creating a copy with isDeleted set and incremented version number.
     * Calling this method also removes all modules that disorder includes.
     *
     * @param id id of the disorder to be deleted
     */
    void deleteDisorder(Long id);

    /**
     * fetches the disorder with given id and version number from the database.
     *
     * @param id      id of the disorder to be fetched
     * @param version version of the disorder to be fetched
     * @return disorder with given id and version number. Returns NULL if disorder does not exist.
     */
    Disorder getSpecificDisorder(Long id, int version);

    /**
     * Gets the latest version of a specific disorder from the database.
     *
     * @param id Id of the disorder to get
     * @return latest version of the disorder with given id, returns NULL if no such if disorder does not exist.
     */
    Disorder getDisorderById(Long id);
}
