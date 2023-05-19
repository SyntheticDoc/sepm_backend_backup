package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.QuestionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.QuestionMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Question;
import at.ac.tuwien.sepm.groupphase.backend.repository.QuestionRepository;
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
public class QuestionDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;

    public QuestionDataGenerator(QuestionRepository questionRepository, QuestionMapper questionMapper) {
        this.questionRepository = questionRepository;
        this.questionMapper = questionMapper;
    }

    @PostConstruct
    private void generateQuestions() {
        LOGGER.debug("Question generation");

        if (questionRepository.findAll().size() > 0) {
            LOGGER.error("Questions already generated");
        } else {
            try {
                List<QuestionDto> questionList;
                ObjectMapper objectMapper = new ObjectMapper();
                questionList = objectMapper.readValue(new ClassPathResource("questionTestData.json").getFile(), new TypeReference<List<QuestionDto>>() {
                });

                LOGGER.debug("Generating {} questions", questionList.size());

                questionList.forEach(q -> {
                    Question temp = questionMapper.questionDtoToQuestion(q);
                    questionRepository.saveAndFlush(temp);
                    LOGGER.debug(temp.toString());
                });
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }
}
