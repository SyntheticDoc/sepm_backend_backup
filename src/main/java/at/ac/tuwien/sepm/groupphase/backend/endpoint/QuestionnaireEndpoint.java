package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.QuestionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.QuestionnaireDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResultInDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.QuestionnaireMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ResultMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.LanguageEnum;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.QuestionnaireServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/questionnaire")
public class QuestionnaireEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final QuestionnaireServiceImpl questionnaireService;
    private final QuestionnaireMapper questionnaireMapper;
    private final ResultMapper resultMapper;

    @Autowired
    public QuestionnaireEndpoint(QuestionnaireServiceImpl questionnaireService, QuestionnaireMapper questionnaireMapper, ResultMapper resultMapper) {
        this.questionnaireService = questionnaireService;
        this.questionnaireMapper = questionnaireMapper;
        this.resultMapper = resultMapper;
    }

    @GetMapping
    @PermitAll
    public QuestionnaireDto requestQuestionnaire(@RequestHeader("Accept-Language") String language) {
        try {
            LanguageEnum.Language responseLanguage = LanguageEnum.Language.valueOf(language);
            System.out.println("got questionnaire request language: " + responseLanguage);
            return questionnaireMapper.questionnaireToQuestionnaireDto(questionnaireService.requestQuestionnaire(), responseLanguage);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Language not supported");
        }
    }

    @PostMapping(value = "/submission/question")
    @PermitAll
    public boolean submitQuestion(@RequestBody QuestionDto questionDto) {
        // TODO: Save answered question to continue another time
        // TODO: Evaluate question and send result
        return true;
    }

    @PostMapping(value = "/submission")
    @PermitAll
    public ResultDto submitQuestionnaire(@RequestHeader("Accept-Language") String language, @RequestBody ResultInDto resultDto) {
        // TODO: Evaluate result in service
        try {
            LanguageEnum.Language responseLanguage = LanguageEnum.Language.valueOf(language);
            return resultMapper.resultToResultDto(questionnaireService.saveResult(resultDto), responseLanguage);
        } catch (NotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @GetMapping(value = "/result")
    @PermitAll // TODO User permissions and result filter
    public List<ResultDto> getResults(@RequestHeader("Accept-Language") String language) {
        LanguageEnum.Language responseLanguage = LanguageEnum.Language.valueOf(language);
        return resultMapper.resultToResultDto(questionnaireService.getResults(), responseLanguage);
    }
}
