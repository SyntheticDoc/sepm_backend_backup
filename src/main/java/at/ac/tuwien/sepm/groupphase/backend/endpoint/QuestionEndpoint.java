package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.QuestionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.QuestionMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.ParsingException;
import at.ac.tuwien.sepm.groupphase.backend.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/question")
public class QuestionEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final QuestionService questionService;
    private final QuestionMapper questionMapper;

    public QuestionEndpoint(QuestionService questionService, QuestionMapper questionMapper) {
        this.questionService = questionService;
        this.questionMapper = questionMapper;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    @PermitAll //TODO PERMIT EXPERT
    public QuestionDto addQuestion(@RequestBody QuestionDto questionDto) {
        try {
            return questionMapper.questionToQuestionDto(questionService.addQuestion(questionDto));
        } catch (ParsingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error during logic check: " + e.getMessage());
        }
    }

    @PutMapping()
    @PermitAll //TODO PERMIT EXPERT
    public QuestionDto updateQuestion(@RequestBody QuestionDto questionDto) {
        try {
            return questionMapper.questionToQuestionDto(questionService.updateQuestion(questionDto));
        } catch (ParsingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error during logic check: " + e.getMessage());
        }
    }

    @DeleteMapping(value = "/{id}")
    @PermitAll //TODO PERMIT EXPERT
    public void deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
    }

    @GetMapping(value = "/question/{id}/{version}")
    @PermitAll
    public QuestionDto getSpecificQuestion(@PathVariable Long id, @PathVariable int version) {
        return questionMapper.questionToQuestionDto(questionService.getSpecificQuestion(id, version));
    }
}
