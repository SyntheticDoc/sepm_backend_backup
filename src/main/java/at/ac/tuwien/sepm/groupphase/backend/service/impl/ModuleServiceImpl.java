package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Module;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ModuleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ModuleMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Question;
import at.ac.tuwien.sepm.groupphase.backend.repository.ModuleRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LogicService;
import at.ac.tuwien.sepm.groupphase.backend.service.ModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class ModuleServiceImpl implements ModuleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ModuleRepository moduleRepository;
    private final ModuleMapper moduleMapper;
    private final LogicService logicService;

    public ModuleServiceImpl(ModuleRepository moduleRepository, ModuleMapper moduleMapper, LogicService logicService) {
        this.moduleRepository = moduleRepository;
        this.moduleMapper = moduleMapper;
        this.logicService = logicService;
    }

    @Override
    public List<Module> getAllModules() {
        return moduleRepository.findLatestAll();
    }

    @Override
    public Module addModule(ModuleDto moduleDto) {
        // Check for parsing error
        logicService.parseString(moduleDto.logic(), null);

        Module newModule = moduleMapper.moduleDtoToModule(moduleDto);
        if (moduleRepository.getNextId() != null) {
            newModule.setId(moduleRepository.getNextId());
        } else {
            newModule.setId(0L);
        }
        newModule.setDeleted(false);
        newModule.setVersion(0);
        return moduleRepository.saveAndFlush(newModule);
    }

    @Override
    public void deleteModule(Long id) {
        Module newModule = new Module(moduleRepository.findLatest(id));
        newModule.setVersion(moduleRepository.findLatest(newModule.getId()).getVersion() + 1);
        newModule.setDeleted(true);
        moduleRepository.save(newModule);
    }

    @Override
    public Module addQuestion(Long id, Long question) {
        Module newModule = new Module(moduleRepository.findLatest(id));

        List<Long> questions = newModule.getQuestions();
        questions.add(question);

        newModule.setQuestions(questions);
        newModule.setVersion(moduleRepository.findLatest(id).getVersion() + 1);

        return moduleRepository.save(newModule);
    }

    @Override
    public Module putModule(Long id, ModuleDto moduleDto) {
        // Check for parsing error
        logicService.parseString(moduleDto.logic(), null);

        Module newModule = moduleMapper.moduleDtoToModule(moduleDto);
        newModule.setVersion(moduleRepository.findLatest(newModule.getId()).getVersion() + 1);

        return moduleRepository.save(newModule);
    }

    @Override
    public void deleteQuestion(Long id, Long questionId) {
        Module newModule = new Module(moduleRepository.findLatest(id));

        List<Long> questions = newModule.getQuestions();
        questions.remove(questionId);

        newModule.setQuestions(questions);
        newModule.setVersion(moduleRepository.findLatest(id).getVersion() + 1);

        moduleRepository.save(newModule);
    }

    @Override
    public Module getById(Long id) {
        return moduleRepository.findLatest(id);
    }
}
