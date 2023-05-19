package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.util.List;

public class Questionnaire {
    private final List<Question> questions;

    public Questionnaire(List<Question> questionList) {
        this.questions = questionList;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
