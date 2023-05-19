package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ModuleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.QuestionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ModuleMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.ParsingException;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.ModuleServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "/api/v1/modules")
public class ModuleEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ModuleServiceImpl moduleService;
    private final ModuleMapper moduleMapper;

    @Autowired
    public ModuleEndpoint(ModuleServiceImpl moduleService, ModuleMapper moduleMapper) {
        this.moduleService = moduleService;
        this.moduleMapper = moduleMapper;
    }

    @GetMapping
    @PermitAll
    public Stream<ModuleDto> getAllModules() {
        return moduleService.getAllModules().stream()
            .map(moduleMapper::moduleToModuleDto);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    @PermitAll //TODO PERMIT EXPERT
    public ModuleDto addModule(@RequestBody ModuleDto moduleDto) {
        try {
            return moduleMapper.moduleToModuleDto(moduleService.addModule(moduleDto));
        } catch (ParsingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error during logic check: " + e.getMessage());
        }
    }

    @DeleteMapping(value = "/{id}")
    @PermitAll //TODO PERMIT EXPERT
    public void deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
    }

    @GetMapping(value = "/{id}")
    @PermitAll
    public ModuleDto getById(@PathVariable Long id) {
        return moduleMapper.moduleToModuleDto(moduleService.getById(id));
    }

    @PutMapping(value = "/{id}/{questionId}")
    @PermitAll //TODO PERMIT EXPERT
    public ModuleDto addQuestion(@PathVariable Long id, @PathVariable Long questionId) {
        return moduleMapper.moduleToModuleDto(moduleService.addQuestion(id, questionId));
    }

    @PutMapping(value = "/{id}")
    @PermitAll //TODO PERMIT EXPERT
    public ModuleDto putModule(@PathVariable Long id, @RequestBody ModuleDto moduleDto) {
        try {
            return moduleMapper.moduleToModuleDto(moduleService.putModule(id, moduleDto));
        } catch (ParsingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error during logic check: " + e.getMessage());
        }
    }

    @DeleteMapping(value = "/{id}/{questionId}")
    @PermitAll //TODO PERMIT EXPERT
    public void deleteQuestion(@PathVariable Long id, @PathVariable Long questionId) {
        moduleService.deleteQuestion(id, questionId);
    }
}
