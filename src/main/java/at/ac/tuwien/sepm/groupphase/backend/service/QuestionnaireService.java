package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResultInDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Questionnaire;
import at.ac.tuwien.sepm.groupphase.backend.entity.Result;

import java.util.List;

public interface QuestionnaireService {

    /**
     * Returns a questionnaire that includes all the latest questions from the database.
     *
     * @return a questionnaire including all the latest questions.
     */
    Questionnaire requestQuestionnaire();

    /**
     * Evaluates and saves the result of a questionnaire in the database.
     *
     * @return the result of the saved questionnaire.
     */
    Result saveResult(ResultInDto questionnaireDto);

    List<Result> getResults();
}
