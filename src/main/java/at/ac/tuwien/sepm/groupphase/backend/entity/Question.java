package at.ac.tuwien.sepm.groupphase.backend.entity;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.IdClass;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.ElementCollection;
import java.util.Map;

@Entity
@IdClass(IdVersionKey.class)
public class Question {
    @Id
    private Long id;

    @Id
    private Integer version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Column
    private Boolean optional = false;

    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

    public Boolean isOptional() {
        return optional;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<LanguageEnum.Language, QuestionStrings> text;

    public void setText(Map<LanguageEnum.Language, QuestionStrings> text) {
        this.text = text;
    }

    public Map<LanguageEnum.Language, QuestionStrings> getText() {
        return text;
    }

    public enum AnswerType { Numeric, Text }

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<LanguageEnum.Language, String> answerPossibilities;

    public void setAnswerPossibilities(Map<LanguageEnum.Language, String> answerPossibilities) {
        this.answerPossibilities = answerPossibilities;
    }

    public Map<LanguageEnum.Language, String> getAnswerPossibilities() {
        return answerPossibilities;
    }

    @Enumerated(EnumType.STRING)
    private AnswerType answerType;

    public void setAnswerType(AnswerType answerType) {
        this.answerType = answerType;
    }

    public AnswerType getAnswerType() {
        return answerType;
    }

    @Column
    private String logic;

    public void setLogic(String logic) {
        this.logic = logic;
    }

    public String getLogic() {
        return this.logic;
    }

    @Column
    private Long dependsOn;

    public Long getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(Long dependsOn) {
        this.dependsOn = dependsOn;
    }

    @Column
    private Boolean deleted = false;

    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public static final class QuestionBuilder {
        private Long id;
        private Integer version;
        private AnswerType answerType;
        private String logic;
        private Boolean optional;
        private Boolean deleted;
        private Long dependsOn;
        private Map<LanguageEnum.Language, QuestionStrings> text;

        private QuestionBuilder() {
        }

        public static Question.QuestionBuilder aQuestion() {
            return new Question.QuestionBuilder();
        }

        public Question.QuestionBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public Question.QuestionBuilder withVersion(Integer version) {
            this.version = version;
            return this;
        }

        public Question.QuestionBuilder withAnswerType(AnswerType answerType) {
            this.answerType = answerType;
            return this;
        }

        public Question.QuestionBuilder withLogic(String logic) {
            this.logic = logic;
            return this;
        }

        public Question.QuestionBuilder withOptional(Boolean optional) {
            this.optional = optional;
            return this;
        }

        public Question.QuestionBuilder withDependsOn(Long dependsOn) {
            this.dependsOn = dependsOn;
            return this;
        }

        public Question.QuestionBuilder withText(Map<LanguageEnum.Language, QuestionStrings> text) {
            this.text = text;
            return this;
        }

        public Question.QuestionBuilder withDeleted(Boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public Question build() {
            Question question = new Question();
            question.setId(id);
            question.setVersion(version);
            question.setAnswerType(answerType);
            question.setLogic(logic);
            question.setOptional(optional);
            question.setText(text);
            question.setDependsOn(dependsOn);
            question.setDeleted(deleted);
            return question;
        }
    }

    @Override
    public String toString() {
        return "Question{"
            + "id=" + id
            + ", version=" + version
            + ", optional=" + optional
            + ", text='" + text + '\''
            + ", answerType=" + answerType
            + ", logic='" + logic + '\''
            + '}';
    }
}
