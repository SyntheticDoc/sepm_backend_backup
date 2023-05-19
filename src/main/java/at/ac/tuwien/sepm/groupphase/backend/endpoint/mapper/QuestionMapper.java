package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.QuestionDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.LanguageEnum;
import at.ac.tuwien.sepm.groupphase.backend.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface QuestionMapper {
    Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Mapping(source = "answerPossibilities", target = "answerPossibilities", qualifiedByName = "stringToList")
    QuestionDto questionToQuestionDto(Question question);

    @Mapping(source = "answerPossibilities", target = "answerPossibilities", qualifiedByName = "listToString")
    Question questionDtoToQuestion(QuestionDto questionDto);

    @Named("stringToList")
    static Map<LanguageEnum.Language, List<String>> stringToList(Map<LanguageEnum.Language, String> input) {
        if (input == null) {
            return null;
        }
        Map<LanguageEnum.Language, List<String>> ret = new HashMap<>();
        for (Map.Entry<LanguageEnum.Language, String> e : input.entrySet()) {
            List<String> unconnected = Arrays.asList(e.getValue().split(";"));
            unconnected.forEach(LOGGER::debug);
            ret.put(e.getKey(), unconnected);
        }
        return ret;
    }

    @Named("listToString")
    static Map<LanguageEnum.Language, String> stringListToString(Map<LanguageEnum.Language, List<String>> input) {
        if (input == null) {
            return null;
        }
        Map<LanguageEnum.Language, String> ret = new HashMap<>();
        for (Map.Entry<LanguageEnum.Language, List<String>> e : input.entrySet()) {
            String connected = e.getValue().stream().reduce((sub, el) -> sub + ";" + el).orElse("");
            LOGGER.debug(connected);
            ret.put(e.getKey(), connected);
        }
        return ret;
    }
}
