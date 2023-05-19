package at.ac.tuwien.sepm.groupphase.backend.service.impl.logic;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.exceptionhandler.FrontendErrorCode;
import at.ac.tuwien.sepm.groupphase.backend.entity.AnsweredQuestion;
import at.ac.tuwien.sepm.groupphase.backend.entity.Disorder;
import at.ac.tuwien.sepm.groupphase.backend.entity.Question;
import at.ac.tuwien.sepm.groupphase.backend.entity.Module;
import at.ac.tuwien.sepm.groupphase.backend.exception.ParsingException;
import at.ac.tuwien.sepm.groupphase.backend.service.LogicService;
import java_cup.runtime.Symbol;
import org.springframework.stereotype.Service;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LogicServiceImpl implements LogicService {
    @Override
    public Boolean testQuestion(AnsweredQuestion question) {
        String logic = question.getQuestion().getLogic();
        logic = logic.replace("q", question.getAnswer().toString());
        Reader logicReader = new StringReader(logic);
        return (Boolean) parseLogic(logicReader, null).value;
    }

    @Override
    public Set<Long> testQuestions(List<AnsweredQuestion> questions) {
        Set<Long> positiveQuestions = new HashSet<>();
        for (AnsweredQuestion q : questions) {
            if (testQuestion(q)) {
                positiveQuestions.add(q.getQuestion().getId());
            }
        }
        return positiveQuestions;
    }

    @Override
    public Boolean testModule(Module module, Set<Long> positiveQuestions) {
        Reader logicReader = new StringReader(module.getLogic());
        return (Boolean) parseLogic(logicReader, positiveQuestions).value;
    }

    @Override
    public Set<Long> testModules(List<Module> modules, Set<Long> positiveQuestions) {
        Set<Long> positiveModules = new HashSet<>();
        for (Module m : modules) {
            if (testModule(m, positiveQuestions)) {
                positiveModules.add(m.getId());
            }
        }
        return positiveModules;
    }

    @Override
    public Boolean testDisorder(Disorder disorder, Set<Long> positiveModules) {
        Reader logicReader = new StringReader(disorder.getLogic());
        return (Boolean) parseLogic(logicReader, positiveModules).value;
    }

    @Override
    public Set<Long> testDisorders(List<Disorder> disorders, Set<Long> positiveModules) {
        Set<Long> positiveDisorders = new HashSet<>();
        for (Disorder d : disorders) {
            if (testDisorder(d, positiveModules)) {
                positiveDisorders.add(d.getId());
            }
        }
        return positiveDisorders;
    }

    private Symbol parseLogic(Reader reader, Set<Long> variables) {
        try {
            Parser parser = new Parser(new Lexer(reader));
            if (variables != null) {
                parser.setVariable(variables);
            }
            Symbol ret = parser.parse();
            reader.close();
            return ret;
        } catch (Exception e) {
            throw new ParsingException(e.getMessage(), FrontendErrorCode.UNKNOWN);
        }
    }

    public Boolean parseString(String in, Set<Long> variables) {
        Reader logicReader = new StringReader(in);
        return (Boolean) parseLogic(logicReader, variables).value;
    }
}
