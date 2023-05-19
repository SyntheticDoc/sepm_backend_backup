package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AnsweredQuestionDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResultInDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Module;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.DisorderRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ModuleRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.QuestionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ResultRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LogicService;
import at.ac.tuwien.sepm.groupphase.backend.service.QuestionnaireService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final QuestionRepository questionnaireRepository;
    private final ResultRepository resultRepository;
    private final LogicService logicService;
    private final ModuleRepository moduleRepository;
    private final DisorderRepository disorderRepository;

    public QuestionnaireServiceImpl(QuestionRepository questionRepository, ResultRepository resultRepository, LogicService logicService, ModuleRepository moduleRepository, DisorderRepository disorderRepository) {
        this.questionnaireRepository = questionRepository;
        this.resultRepository = resultRepository;
        this.logicService = logicService;
        this.moduleRepository = moduleRepository;
        this.disorderRepository = disorderRepository;
    }

    @Override
    public Questionnaire requestQuestionnaire() {
        List<Question> questions = new ArrayList<>(questionnaireRepository.findLatestAll());
        questions.removeIf(q -> {
            if (q.isDeleted() != null) {
                return q.isDeleted();
            } else {
                return false;
            }
        });
        return new Questionnaire(questions);
    }

    @Override
    public Result saveResult(ResultInDto resultDto) {
        if (resultDto == null || resultDto.answeredQuestions() == null) {
            return null;
        }
        Result result = new Result();
        List<Question> positiveQuestions = new ArrayList<>();
        Set<Long> positiveQuestionsIds = new HashSet<>();
        List<Module> positiveModules = new ArrayList<>();
        Set<Long> positiveModulesIds = new HashSet<>();
        List<Disorder> positiveDisorders = new ArrayList<>();

        for (AnsweredQuestionDto q : resultDto.answeredQuestions()) {
            AnsweredQuestion answeredQuestion = new AnsweredQuestion();

            if (!questionnaireRepository.existsById(new IdVersionKey(q.id(), q.version()))) {
                throw new NotFoundException("Nonexistent question in questionnaire");
            }

            answeredQuestion.setQuestion(questionnaireRepository.getById(new IdVersionKey(q.id(), q.version())));
            answeredQuestion.setAnswer(q.answer());
            result.addAnsweredQuestion(answeredQuestion);

            if (logicService.testQuestion(answeredQuestion)) {
                positiveQuestions.add(answeredQuestion.getQuestion());
                positiveQuestionsIds.add(answeredQuestion.getQuestion().getId());
            }
        }
        result.setPositiveQuestions(positiveQuestions);

        for (Module m : moduleRepository.findLatestAll()) {
            if (logicService.testModule(m, positiveQuestionsIds)) {
                positiveModules.add(m);
                positiveModulesIds.add(m.getId());
            }
        }
        result.setPositiveModules(positiveModules);

        for (Disorder d : disorderRepository.findLatestAll()) {
            if (logicService.testDisorder(d, positiveModulesIds)) {
                positiveDisorders.add(d);
            }
        }
        result.setPositiveDisorders(positiveDisorders);

        return resultRepository.save(result);
    }

    @Override
    public List<Result> getResults() {
        return resultRepository.findAll();
    }
}