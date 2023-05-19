package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResultDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.LanguageEnum;
import at.ac.tuwien.sepm.groupphase.backend.entity.Result;
import org.mapstruct.*;

import java.util.List;

@Mapper(uses = {AnsweredQuestionMapper.class, DisorderMapper.class})
public interface ResultMapper {

    @Named("singleResult")
    @Mapping(source = "id", target = "resultId")
    ResultDto resultToResultDto(Result result, @Context LanguageEnum.Language language);

    @IterableMapping(qualifiedByName = "singleResult")
    List<ResultDto> resultToResultDto(List<Result> results, @Context LanguageEnum.Language language);

    //public abstract Result resultDtoToResult(ResultDto resultDto, @Context LanguageEnum.Language language);
}
