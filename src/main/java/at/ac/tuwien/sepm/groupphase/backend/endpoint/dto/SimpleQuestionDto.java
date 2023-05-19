package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.LanguageEnum;
import at.ac.tuwien.sepm.groupphase.backend.entity.Question;
import at.ac.tuwien.sepm.groupphase.backend.entity.QuestionStrings;

import java.util.List;
import java.util.Map;

public record SimpleQuestionDto(Long id, Integer version, Question.AnswerType answerType, Boolean optional,
                                QuestionStrings text, Long dependsOn, List<String> answerPossibilities) {
}
