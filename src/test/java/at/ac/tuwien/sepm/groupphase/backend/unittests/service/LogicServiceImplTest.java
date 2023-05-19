package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.ExecutionTimeTestExecutionListener;
import at.ac.tuwien.sepm.groupphase.backend.entity.AnsweredQuestion;
import at.ac.tuwien.sepm.groupphase.backend.entity.Disorder;
import at.ac.tuwien.sepm.groupphase.backend.entity.Question;
import at.ac.tuwien.sepm.groupphase.backend.entity.Module;
import at.ac.tuwien.sepm.groupphase.backend.service.LogicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import at.ac.tuwien.sepm.groupphase.backend.exception.ParsingException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, ExecutionTimeTestExecutionListener.class})
public class LogicServiceImplTest {

    @Autowired
    private LogicService logicService;


    @Test
    public void parseQuestionsThenModulesThenDisorders()
    {
        List<AnsweredQuestion> questions = Arrays.asList(
            AnsweredQuestion.AnsweredQuestionBuilder.aQuestion().withQuestion(Question.QuestionBuilder.aQuestion().withId(0L).withLogic("(q = 3)").build()).withAnswer(3).build(),
            AnsweredQuestion.AnsweredQuestionBuilder.aQuestion().withQuestion(Question.QuestionBuilder.aQuestion().withId(1L).withLogic("(q = 2)").build()).withAnswer(2).build(),
            AnsweredQuestion.AnsweredQuestionBuilder.aQuestion().withQuestion(Question.QuestionBuilder.aQuestion().withId(2L).withLogic("(q = 3)").build()).withAnswer(1).build()
        );

        Set<Long> positiveQuestions = logicService.testQuestions(questions);


        List<Module> modules = Arrays.asList(
            Module.ModuleBuilder.aModule().withId(0L).withLogic("[q0,q1,q2]>1").build(),
            Module.ModuleBuilder.aModule().withId(1L).withLogic("q0 AND q2").build()
        );

        Set<Long> positiveModules = logicService.testModules(modules, positiveQuestions);

        List<Disorder> disorders = Arrays.asList(
            Disorder.DisorderBuilder.aDisorder().withId(0L).withLogic("m0 OR m1").build(),
            Disorder.DisorderBuilder.aDisorder().withId(1L).withLogic("m0 AND m1").build()
        );

        Set<Long> positiveDisorders = logicService.testDisorders(disorders, positiveModules);

        assertAll(
            () -> assertEquals(true, positiveQuestions.contains(0L)),
            () -> assertEquals(true, positiveQuestions.contains(1L)),
            () -> assertEquals(false, positiveQuestions.contains(2L)),
            () -> assertEquals(true, positiveModules.contains(0L)),
            () -> assertEquals(false, positiveModules.contains(1L)),
            () -> assertEquals(true, positiveDisorders.contains(0L)),
            () -> assertEquals(false, positiveDisorders.contains(1L))
        );
    }

    @Test
    public void parseStringBooleanExpressions()
    {
        Boolean tAndT = logicService.parseString("TRUE AND TRUE", null);
        Boolean tOrT = logicService.parseString("TRUE OR TRUE", null);
        Boolean fOrF = logicService.parseString("FALSE OR FALSE", null);
        Boolean tAndF = logicService.parseString("TRUE AND FALSE", null);
        Boolean tAndBracketTOrF = logicService.parseString("TRUE AND (TRUE OR FALSE)", null);
        Boolean fAndBracketTAndT = logicService.parseString("FALSE AND (TRUE AND TRUE)", null);
        Boolean tIsT = logicService.parseString("TRUE = TRUE", null);
        Boolean tIsF = logicService.parseString("TRUE = FALSE", null);
        assertAll(
            () -> assertEquals(true, tAndT),
            () -> assertEquals(true, tOrT),
            () -> assertEquals(false, fOrF),
            () -> assertEquals(false, tAndF),
            () -> assertEquals(true, tAndBracketTOrF),
            () -> assertEquals(false, fAndBracketTAndT),
            () -> assertEquals(true, tIsT),
            () -> assertEquals(false, tIsF)
        );
    }

    @Test
    public void parseStringNumberExpressions()
    {
        Boolean oneGreaterFive = logicService.parseString("1>5", null);
        Boolean sixGreaterFive = logicService.parseString("6>5", null);
        Boolean oneGreaterOne = logicService.parseString("1>1", null);
        Boolean fourEqualsFive = logicService.parseString("4=5", null);
        Boolean sixEqualsSix = logicService.parseString("6=6", null);
        Boolean sixGreaterEqualsSix = logicService.parseString("6>=6", null);
        Boolean sixGreaterEqualsFive = logicService.parseString("6>=5", null);
        Boolean sixGreaterEqualsSeven = logicService.parseString("6>=7", null);
        assertAll(
            () -> assertEquals(false, oneGreaterFive),
            () -> assertEquals(true, sixGreaterFive),
            () -> assertEquals(false, oneGreaterOne),
            () -> assertEquals(false, fourEqualsFive),
            () -> assertEquals(true, sixEqualsSix),
            () -> assertEquals(true, sixGreaterEqualsSix),
            () -> assertEquals(true, sixGreaterEqualsFive),
            () -> assertEquals(false, sixGreaterEqualsSeven)
        );
    }

    @Test
    public void parseStringEnumerationExpressions()
    {
        Boolean fiveInSevenIsFive = logicService.parseString("[TRUE, TRUE, TRUE, FALSE, TRUE, TRUE, FALSE]=5", null);
        Boolean fiveInSevenIsGreaterFour = logicService.parseString("[TRUE, TRUE, TRUE, FALSE, TRUE, TRUE, FALSE]>4", null);
        Boolean fiveInSevenIsLessSix = logicService.parseString("[TRUE, TRUE, TRUE, FALSE, TRUE, TRUE, FALSE]<6", null);
        Boolean fiveInSevenIsSix = logicService.parseString("[TRUE, TRUE, TRUE, FALSE, TRUE, TRUE, FALSE]=6", null);
        Boolean oneInOneIsGreaterZero = logicService.parseString("[TRUE]>0", null);
        Boolean oneInOneIsGreaterOne = logicService.parseString("[TRUE]>1", null);
        Boolean oneInOneIsGreaterEqualsOne = logicService.parseString("[TRUE]>=1", null);
        assertAll(
            () -> assertEquals(true, fiveInSevenIsFive),
            () -> assertEquals(true, fiveInSevenIsGreaterFour),
            () -> assertEquals(true, fiveInSevenIsLessSix),
            () -> assertEquals(false, fiveInSevenIsSix),
            () -> assertEquals(true, oneInOneIsGreaterZero),
            () -> assertEquals(false, oneInOneIsGreaterOne),
            () -> assertEquals(true, oneInOneIsGreaterEqualsOne)
        );
    }

    @Test
    public void parseStringVariableExpressions()
    {
        Boolean oneAndTwoWithOne = logicService.parseString("q1 AND q2", new HashSet<Long>(Arrays.asList(1L)));
        Boolean oneAndTwoWithOneAndTwo = logicService.parseString("q1 AND q2", new HashSet<Long>(Arrays.asList(1L, 2L)));
        Boolean enumerationOneTwoThreeWithOneThreeIsTwo = logicService.parseString("[q1,q2,q3]=2", new HashSet<Long>(Arrays.asList(1L,3L)));
        Boolean oneOrTwoEmptyVariables = logicService.parseString("q1 OR q2", null);
        Boolean zeroAndTwoWithZeroAndOne = logicService.parseString("q0 AND q2", new HashSet<Long>(Arrays.asList(0L, 1L)));
        Boolean deMorgan1 = logicService.parseString("NOT q1 AND q2 = NOT q1 OR NOT q2", new HashSet<>(Arrays.asList(1L, 2L)));
        Boolean deMorgan2 = logicService.parseString("(NOT (q1 AND q2)) = ((NOT q1) OR (NOT q2))", new HashSet<>(Arrays.asList(1L)));
        Boolean deMorgan3 = logicService.parseString("(NOT (q1 AND q2)) = ((NOT q1) OR (NOT q2))", new HashSet<>(Arrays.asList(2L)));
        Boolean deMorgan4 = logicService.parseString("(NOT (q1 AND q2)) = ((NOT q1) OR (NOT q2))", null);
        Boolean moduleZeroAndTwoWithZeroAndTwo = logicService.parseString("m0 AND m2", new HashSet<Long>(Arrays.asList(0L, 2L)));
        Boolean moduleZeroAndTwoWithZero = logicService.parseString("m0 AND m2", new HashSet<Long>(Arrays.asList(0L)));
        assertAll(
            () -> assertEquals(false, oneAndTwoWithOne),
            () -> assertEquals(true, oneAndTwoWithOneAndTwo),
            () -> assertEquals(true, enumerationOneTwoThreeWithOneThreeIsTwo),
            () -> assertEquals(false, oneOrTwoEmptyVariables),
            () -> assertEquals(false, zeroAndTwoWithZeroAndOne),
            () -> assertEquals(true, deMorgan1),
            () -> assertEquals(true, deMorgan2),
            () -> assertEquals(true, deMorgan3),
            () -> assertEquals(true, deMorgan4),
            () -> assertEquals(true, moduleZeroAndTwoWithZeroAndTwo),
            () -> assertEquals(false, moduleZeroAndTwoWithZero)
        );
    }

    @Test
    public void parseStringWithUnknownSymbol()
    {
        Throwable exceptionThatWasThrown = assertThrows(ParsingException.class, () -> logicService.parseString("{TRUE}", null));
        assertEquals("Illegal character <{>", exceptionThatWasThrown.getMessage());
    }

    @Test
    public void parseStringWithSyntaxError()
    {
        assertThrows(ParsingException.class, () -> logicService.parseString("(TRUE", null));
    }
}
