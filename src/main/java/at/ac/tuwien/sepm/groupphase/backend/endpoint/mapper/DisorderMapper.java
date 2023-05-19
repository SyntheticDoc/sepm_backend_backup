package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DisorderDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleDisorderDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Disorder;
import at.ac.tuwien.sepm.groupphase.backend.entity.DisorderStrings;
import at.ac.tuwien.sepm.groupphase.backend.entity.LanguageEnum;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Mapper
public interface DisorderMapper {

    DisorderDto disorderToDisorderDto(Disorder disorder);

    Disorder disorderDtoToDisorder(DisorderDto disorderDto);

    @Mapping(source = "text", target = "text", qualifiedByName = "stringsByLanguage")
    SimpleDisorderDto disorderToSimpleDisorderDto(Disorder disorder, @Context LanguageEnum.Language language);

    @Named("stringsByLanguage")
    default DisorderStrings stringsByLanguage(Map<LanguageEnum.Language, DisorderStrings> text, @Context LanguageEnum.Language language) {
        return text.get(language);
    }

}
