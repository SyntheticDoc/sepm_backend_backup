package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.exceptionhandler.FrontendErrorCode;
import at.ac.tuwien.sepm.groupphase.backend.entity.AnsweredQuestion;
import at.ac.tuwien.sepm.groupphase.backend.entity.Disorder;
import at.ac.tuwien.sepm.groupphase.backend.entity.Module;
import at.ac.tuwien.sepm.groupphase.backend.entity.Question;
import at.ac.tuwien.sepm.groupphase.backend.exception.ParsingException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Set;

public interface LogicService {
    /**
     * Returns true if the answer of the question satisfies its logic.
     *
     * @param question question that is evaluated.
     * @return true if the question evaluation is true, false otherwise.
     */
    public Boolean testQuestion(AnsweredQuestion question);

    /**
     * Returns a set of ids of questions that are evaluated as positive from the parameter question list.
     *
     * @param questions a lists of questions that are evaluated over.
     * @return a set of ids of positive questions.
     */
    public Set<Long> testQuestions(List<AnsweredQuestion> questions);

    /**
     * Returns true if the logic of the provided module satisfies its logic under the given question setting.
     *
     * @param module the module that is evaluated.
     * @param positiveQuestions a set of ids of positive questions that determines the variables in the module logic.
     * @return true if the module evaluation is true, false otherwise.
     */
    public Boolean testModule(Module module, Set<Long> positiveQuestions);

    /**
     * Returns a set of ids of modules that are evaluated as positive from the parameter module list and question setting.
     *
     * @param modules the list of modules which are evaluated.
     * @param positiveQuestions a set of ids of positive questions that determines the variables in the module logic.
     * @return a set of ids of positive modules.
     */
    public Set<Long> testModules(List<Module> modules, Set<Long> positiveQuestions);

    /**
     * Returns true if the logic of the provided disorder satisfies its logic under the given module setting.
     *
     * @param disorder the disorder that is evaluated.
     * @param positiveModules a set of ids of positive modules that determines the variables in the disorder logic.
     * @return true if the disorder evaluation is true, false otherwise.
     */
    public Boolean testDisorder(Disorder disorder, Set<Long> positiveModules);

    /**
     * Returns a set of ids of disorders that are evaluated as positive from the parameter disorder list and module setting.
     *
     * @param disorders the list of disorders which are evaluated.
     * @param positiveModules a set of ids of positive modules that determines the variables in the disorder logic.
     * @return a set of ids of positive disorders.
     */
    public Set<Long> testDisorders(List<Disorder> disorders, Set<Long> positiveModules);

    public Boolean parseString(String in, Set<Long> variables);
}
