package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.QuestionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.QuestionMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.IdVersionKey;
import at.ac.tuwien.sepm.groupphase.backend.entity.Question;
import at.ac.tuwien.sepm.groupphase.backend.repository.QuestionRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LogicService;
import at.ac.tuwien.sepm.groupphase.backend.service.QuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionServiceImpl implements QuestionService {
    QuestionRepository questionRepository;
    QuestionMapper questionMapper;
    LogicService logicService;

    public QuestionServiceImpl(QuestionRepository questionRepository, QuestionMapper questionMapper, LogicService logicService) {
        this.questionRepository = questionRepository;
        this.questionMapper = questionMapper;
        this.logicService = logicService;
    }

    @Override
    public Question addQuestion(QuestionDto question) {
        // Check for parsing error
        logicService.parseString(question.logic().replace("q", "0"), null);

        Question newQuestion = questionMapper.questionDtoToQuestion(question);
        Long id = questionRepository.getNextId();
        if (id == null) {
            id = 0L;
        }
        newQuestion.setId(id);
        newQuestion.setDeleted(false);
        newQuestion.setVersion(0);
        return questionRepository.saveAndFlush(newQuestion);
    }

    @Override
    public Question updateQuestion(QuestionDto question) {
        //Check for parsing error
        logicService.parseString(question.logic().replace("q", "0"), null);

        Question newQuestion = questionMapper.questionDtoToQuestion(question);
        newQuestion.setVersion(questionRepository.findLatest(newQuestion.getId()).getVersion() + 1);
        return questionRepository.save(newQuestion);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        Question newQuestion = questionRepository.findLatest(id);
        newQuestion.setVersion(questionRepository.findLatest(newQuestion.getId()).getVersion() + 1);
        newQuestion.setDeleted(true);
        questionRepository.removeDependsOn(id);
    }

    @Override
    public Question getSpecificQuestion(Long id, int version) {
        return questionRepository.findById(new IdVersionKey(id, version)).orElse(null);
    }
}
