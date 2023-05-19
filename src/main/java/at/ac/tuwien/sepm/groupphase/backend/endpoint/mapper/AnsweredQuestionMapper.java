package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AnsweredQuestionDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.AnsweredQuestion;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface AnsweredQuestionMapper {
    @Named("singleAnsweredQuestion")
    @Mapping(source = "question.id", target = "id")
    @Mapping(source = "question.version", target = "version")
    AnsweredQuestionDto answeredQuestionToAnsweredQuestionDto(AnsweredQuestion answeredQuestion);

    @IterableMapping(qualifiedByName = "singleAnsweredQuestion")
    List<AnsweredQuestionDto> answeredQuestionToAnsweredQuestionDto(List<AnsweredQuestion> answeredQuestion);
}
