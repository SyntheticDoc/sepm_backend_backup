package at.ac.tuwien.sepm.groupphase.backend.unittests.service;


import at.ac.tuwien.sepm.groupphase.backend.repository.QuestionRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.QuestionService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QuestionServiceTest {

    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    PlatformTransactionManager txm;

    TransactionStatus txstatus;

    @BeforeAll
    void setUp() {
        questionRepository.deleteAll();
    }

    @BeforeEach
    void setUpOne() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        txstatus = txm.getTransaction(def);
        assumeTrue(txstatus.isNewTransaction());
        txstatus.setRollbackOnly();
    }

    @AfterEach
    void tearDown() {
        txm.rollback(txstatus);
    }

    @Test
    @DisplayName("Registration should not allow duplicate username")
    public void registrationAddUserThatAlreadyExists() {
        //QuestionDto newQuestion = new QuestionDto();
        //questionService.addQuestion(newQuestion);
        //assertThrows(CollisionException.class, () -> questionService.addQuestion(newQuestion));
    }


}
