package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DisorderDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.DisorderMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Disorder;
import at.ac.tuwien.sepm.groupphase.backend.repository.DisorderRepository;
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
public class DisorderDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final DisorderRepository disorderRepository;
    private final DisorderMapper disorderMapper;

    public DisorderDataGenerator(DisorderRepository moduleRepository, DisorderMapper moduleMapper) {
        this.disorderRepository = moduleRepository;
        this.disorderMapper = moduleMapper;
    }

    @PostConstruct
    private void generateDisorder() {
        LOGGER.debug("Disorder generation");

        if (disorderRepository.findAll().size() > 0) {
            LOGGER.error("Disorders already generated");
        } else {
            try {
                List<DisorderDto> disorderList;
                ObjectMapper objectMapper = new ObjectMapper();
                disorderList = objectMapper.readValue(new ClassPathResource("disorderTestData.json").getFile(), new TypeReference<List<DisorderDto>>() {
                });

                LOGGER.debug("Generating {} questions", disorderList.size());

                disorderList.forEach(q -> {
                    Disorder temp = disorderMapper.disorderDtoToDisorder(q);
                    disorderRepository.saveAndFlush(temp);
                    LOGGER.debug(temp.toString());
                });
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }
}


