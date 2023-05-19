package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.QuestionEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.QuestionDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Question;
import at.ac.tuwien.sepm.groupphase.backend.service.QuestionService;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.QuestionMapperImpl;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class QuestionEndpointTest {
    @Mock
    private QuestionService questionService;
    @Mock
    private QuestionMapperImpl questionMapper;
    @InjectMocks
    private QuestionEndpoint questionEndpoint;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddQuestion() throws Exception {
        QuestionDto questionDto = null;
        Question addQuestionReturnValue = null;
        when(questionService.addQuestion((QuestionDto) null)).thenReturn(addQuestionReturnValue);
        QuestionDto questionToQuestionDtoReturnValue = null;
        when(questionMapper.questionToQuestionDto((Question) null)).thenReturn(questionToQuestionDtoReturnValue);
        QuestionDto addQuestionRetReturnValue = questionEndpoint.addQuestion(questionDto);
        QuestionDto sutExpected = null;
        assertEquals(sutExpected, addQuestionRetReturnValue);
    }

    @Test
    public void testUpdateQuestion() throws Exception {
        QuestionDto questionDto = null;
        Question updateQuestionReturnValue = null;
        when(questionService.updateQuestion((QuestionDto) null)).thenReturn(updateQuestionReturnValue);
        QuestionDto questionToQuestionDtoReturnValue1 = null;
        when(questionMapper.questionToQuestionDto((Question) null)).thenReturn(questionToQuestionDtoReturnValue1);
        QuestionDto updateQuestionRetReturnValue = questionEndpoint.updateQuestion(questionDto);
        QuestionDto sutExpected = null;
        assertEquals(sutExpected, updateQuestionRetReturnValue);
    }

    @Test
    public void testDeleteQuestion() throws Exception {
        Long id = 1L;
        questionEndpoint.deleteQuestion(id);
    }


    @Test
    public void TestgetSpecificQuestion() throws Exception {
        Long id = 1L;
        int version = 1;
        Question getSpecificQuestionReturnValue = null;
        when(questionService.getSpecificQuestion(1L, 1)).thenReturn(getSpecificQuestionReturnValue);
        QuestionDto questionToQuestionDtoReturnValue2 = null;
        when(questionMapper.questionToQuestionDto((Question) null)).thenReturn(questionToQuestionDtoReturnValue2);
        QuestionDto getSpecificQuestionRetReturnValue = questionEndpoint.getSpecificQuestion(id, version);
        QuestionDto sutExpected = null;
        assertEquals(sutExpected, getSpecificQuestionRetReturnValue);
    }
}

