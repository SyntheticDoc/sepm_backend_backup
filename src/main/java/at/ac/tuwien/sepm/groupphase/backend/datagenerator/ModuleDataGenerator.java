package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Module;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ModuleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ModuleMapper;
import at.ac.tuwien.sepm.groupphase.backend.repository.ModuleRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;

@Component
@Profile("generateData")
public class ModuleDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ModuleRepository moduleRepository;
    private final ModuleMapper moduleMapper;

    public ModuleDataGenerator(ModuleRepository moduleRepository, ModuleMapper moduleMapper) {
        this.moduleRepository = moduleRepository;
        this.moduleMapper = moduleMapper;
    }

    @PostConstruct
    private void generateModules() {
        LOGGER.debug("Module generation");

        if (moduleRepository.findAll().size() > 0) {
            LOGGER.error("Modules already generated");
        } else {
            try {
                List<ModuleDto> moduleList;
                ObjectMapper objectMapper = new ObjectMapper();
                moduleList = objectMapper.readValue(new ClassPathResource("moduleTestData.json").getFile(), new TypeReference<List<ModuleDto>>() {
                });

                LOGGER.debug("Generating {} questions", moduleList.size());

                moduleList.forEach(q -> {
                    Module temp = moduleMapper.moduleDtoToModule(q);
                    moduleRepository.saveAndFlush(temp);
                    LOGGER.debug(temp.toString());
                });
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }
}

