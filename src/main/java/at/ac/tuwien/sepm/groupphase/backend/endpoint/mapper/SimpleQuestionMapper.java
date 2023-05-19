package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleQuestionDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.LanguageEnum;
import at.ac.tuwien.sepm.groupphase.backend.entity.Question;
import at.ac.tuwien.sepm.groupphase.backend.entity.QuestionStrings;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Mapper
public interface SimpleQuestionMapper {
    @Mapping(source = "answerPossibilities", target = "answerPossibilities", qualifiedByName = "stringToListByLanguage")
    @Mapping(source = "text", target = "text", qualifiedByName = "stringsByLanguage")
    SimpleQuestionDto questionToSimpleQuestion(Question question, @Context LanguageEnum.Language language);

    @Named("stringToListByLanguage")
    default List<String> stringToListByLanguage(Map<LanguageEnum.Language, String> answerPossibilities, @Context LanguageEnum.Language language) {
        String connected = answerPossibilities.get(language);
        if (connected == null) {
            return null;
        }
        return Arrays.asList(connected.split(";"));
    }

    @Named("stringsByLanguage")
    default QuestionStrings stringsByLanguage(Map<LanguageEnum.Language, QuestionStrings> text, @Context LanguageEnum.Language language) {
        return text.get(language);
    }
}
