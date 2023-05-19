package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Result {
    @Id
    @GeneratedValue
    private Long id;

    private String userHash;

    // TODO: Remove!
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserHash() {
        return userHash;
    }

    public void setUserHash(String userHash) {
        this.userHash = userHash;
    }

    // TODO: Remove!
    public String getValue() {
        return value;
    }

    // TODO: Remove!
    public void setValue(String value) {
        this.value = value;
    }

    @ElementCollection(fetch = FetchType.LAZY)
    private List<AnsweredQuestion> answeredQuestions = new ArrayList<>();

    public void setAnsweredQuestions(List<AnsweredQuestion> answeredQuestions) {
        this.answeredQuestions = answeredQuestions;
    }

    public void addAnsweredQuestion(AnsweredQuestion answeredQuestion) {
        this.answeredQuestions.add(answeredQuestion);
    }

    public List<AnsweredQuestion> getAnsweredQuestions() {
        return answeredQuestions;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Disorder> positiveDisorders;

    public void setPositiveDisorders(List<Disorder> positiveDisorders) {
        this.positiveDisorders = positiveDisorders;
    }

    public List<Disorder> getPositiveDisorders() {
        return positiveDisorders;
    }

    @ManyToMany
    private List<Question> positiveQuestions;

    public List<Question> getPositiveQuestions() {
        return positiveQuestions;
    }

    public void setPositiveQuestions(List<Question> positiveQuestions) {
        this.positiveQuestions = positiveQuestions;
    }

    @ManyToMany
    private List<Module> positiveModules;

    public List<Module> getPositiveModules() {
        return positiveModules;
    }

    public void setPositiveModules(List<Module> positiveModules) {
        this.positiveModules = positiveModules;
    }
}
