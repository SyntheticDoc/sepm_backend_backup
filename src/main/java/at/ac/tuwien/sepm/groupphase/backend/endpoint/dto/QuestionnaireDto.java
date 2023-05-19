package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Questionnaire;

import java.util.List;

public record QuestionnaireDto(List<SimpleQuestionDto> questions) {
}
