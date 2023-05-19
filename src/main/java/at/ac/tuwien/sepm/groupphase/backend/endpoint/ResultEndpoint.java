package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ResultMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.LanguageEnum;
import at.ac.tuwien.sepm.groupphase.backend.service.ResultService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.ResultServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "/api/v1/results")
public class ResultEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ResultService resultService;
    private final ResultMapper resultMapper;

    @Autowired
    public ResultEndpoint(ResultServiceImpl resultService, ResultMapper resultMapper) {
        this.resultService = resultService;
        this.resultMapper = resultMapper;
    }

    @GetMapping
    @PermitAll
    public Stream<ResultDto> getAllResults(@RequestHeader("Accept-Language") String language) {
        LanguageEnum.Language responseLanguage = LanguageEnum.Language.valueOf(language);
        return resultMapper.resultToResultDto(resultService.getAllResults(), responseLanguage).stream();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    @PermitAll
    public ResultDto addResult(@RequestHeader("Accept-Language") String language, @RequestBody ResultDto resultDto) {
        //LOGGER.debug("test");
        LanguageEnum.Language responseLanguage = LanguageEnum.Language.valueOf(language);
        return resultMapper.resultToResultDto(resultService.addResult(resultDto), responseLanguage);
    }

    @DeleteMapping(value = "/{id}")
    @PermitAll
    public void deleteResult(@PathVariable Long id) {
        resultService.deleteResult(id);
    }

    @PutMapping(value = "/{id}")
    @PermitAll
    public ResultDto updateResult(@RequestHeader("Accept-Language") String language, @PathVariable Long id, @RequestBody ResultDto resultDto) {
        LanguageEnum.Language responseLanguage = LanguageEnum.Language.valueOf(language);
        return resultMapper.resultToResultDto(resultService.putResult(id, resultDto), responseLanguage);
    }
}
