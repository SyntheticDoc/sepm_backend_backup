package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Disorder;
import at.ac.tuwien.sepm.groupphase.backend.entity.Module;

import java.util.List;

public record ResultDto(Long resultId, List<AnsweredQuestionDto> answeredQuestions, List<SimpleDisorderDto> positiveDisorders, List<ModuleDto> positiveModules) {
}
