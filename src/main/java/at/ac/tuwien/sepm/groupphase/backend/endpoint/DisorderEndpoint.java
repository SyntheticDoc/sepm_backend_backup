package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DisorderDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleDisorderDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.DisorderMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.LanguageEnum;
import at.ac.tuwien.sepm.groupphase.backend.exception.ParsingException;
import at.ac.tuwien.sepm.groupphase.backend.service.DisorderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.PermitAll;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "/api/v1/disorder")
public class DisorderEndpoint {
    private final DisorderService disorderService;
    private final DisorderMapper disorderMapper;

    public DisorderEndpoint(DisorderService disorderService, DisorderMapper disorderMapper) {
        this.disorderService = disorderService;
        this.disorderMapper = disorderMapper;
    }

    @GetMapping
    @PermitAll
    public Stream<SimpleDisorderDto> getAllDisorders(@RequestHeader("Accept-Language") String language) {
        LanguageEnum.Language responseLanguage = LanguageEnum.Language.valueOf(language);
        return disorderService.getAllDisorders().stream().map(disorder -> disorderMapper.disorderToSimpleDisorderDto(disorder, responseLanguage));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    @PermitAll //ToDo Permit Expert only
    public DisorderDto addDisorder(@RequestBody DisorderDto disorderDto) {
        try {
            return disorderMapper.disorderToDisorderDto(disorderService.addDisorder(disorderDto));
        } catch (
        ParsingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error during logic check: " + e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    @PermitAll //ToDo Permit Expert only
    public DisorderDto updateDisorder(@RequestBody DisorderDto disorderDto) {
        try {
            return disorderMapper.disorderToDisorderDto(disorderService.updateDisorder(disorderDto));
        } catch (ParsingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error during logic check: " + e.getMessage());
        }
    }

    @DeleteMapping(value = "/{id}")
    @PermitAll //ToDo Permit Expert only
    public void deleteDisorder(@PathVariable Long id) {
        disorderService.deleteDisorder(id);
    }

    @GetMapping(value = "/{id}/{version}")
    @PermitAll
    public DisorderDto getSpecificDisorder(@PathVariable Long id, @PathVariable int version) {
        return disorderMapper.disorderToDisorderDto(disorderService.getSpecificDisorder(id, version));
    }

    @GetMapping(value = "/{id}")
    @PermitAll
    public SimpleDisorderDto getDisorderById(@RequestHeader("Accept-Language") String language, @PathVariable Long id) {
        LanguageEnum.Language responseLanguage = LanguageEnum.Language.valueOf(language);
        System.out.println("got disorder request language: " + responseLanguage);
        return disorderMapper.disorderToSimpleDisorderDto(disorderService.getDisorderById(id), responseLanguage);
    }
}
