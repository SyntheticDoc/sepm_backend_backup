package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.QuestionnaireDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.LanguageEnum;
import at.ac.tuwien.sepm.groupphase.backend.entity.Questionnaire;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper(uses = {SimpleQuestionMapper.class})
public interface QuestionnaireMapper {

    QuestionnaireDto questionnaireToQuestionnaireDto(Questionnaire questionnaire, @Context LanguageEnum.Language language);
}
