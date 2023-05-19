package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.LanguageEnum;
import at.ac.tuwien.sepm.groupphase.backend.entity.Question;
import at.ac.tuwien.sepm.groupphase.backend.entity.QuestionStrings;

import java.util.List;
import java.util.Map;

public record QuestionDto(Long id, Integer version, Question.AnswerType answerType, String logic, Boolean optional,
                          Map<LanguageEnum.Language, QuestionStrings> text, Long dependsOn, Boolean deleted,
                          Map<LanguageEnum.Language, List<String>> answerPossibilities) {
}
