package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.Map;

@Embeddable
public class AnsweredQuestion {
    @OneToOne(fetch = FetchType.LAZY)
    Question question;

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }

    Integer answer;

    public void setAnswer(Integer answer) {
        this.answer = answer;
    }

    public Integer getAnswer() {
        return answer;
    }

    public static final class AnsweredQuestionBuilder {
        private Question question;
        private Integer answer;

        private AnsweredQuestionBuilder() {
        }

        public static AnsweredQuestion.AnsweredQuestionBuilder aQuestion() {
            return new AnsweredQuestion.AnsweredQuestionBuilder();
        }

        public AnsweredQuestion.AnsweredQuestionBuilder withQuestion(Question question) {
            this.question = question;
            return this;
        }

        public AnsweredQuestion.AnsweredQuestionBuilder withAnswer(Integer answer) {
            this.answer = answer;
            return this;
        }

        public AnsweredQuestion build() {
            AnsweredQuestion question = new AnsweredQuestion();
            question.setQuestion(this.question);
            question.setAnswer(answer);
            return question;
        }
    }
}
