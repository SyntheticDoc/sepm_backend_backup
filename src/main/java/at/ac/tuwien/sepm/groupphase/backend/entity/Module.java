package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.util.List;

@Entity
@IdClass(IdVersionKey.class)
public class Module {
    @Id
    private Long id;

    @Id
    private Integer version;

    @ElementCollection
    private List<Long> questions;

    @Column
    private String logic;

    @Column
    private Boolean deleted = false;

    public Module() {}

    public Module(Module newModule) {
        id = newModule.getId();
        version = newModule.getVersion();
        questions = newModule.getQuestions();
        logic = newModule.getLogic();
        deleted = newModule.getDeleted();
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<Long> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Long> questions) {
        this.questions = questions;
    }

    public String getLogic() {
        return logic;
    }

    public void setLogic(String logic) {
        this.logic = logic;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public static final class ModuleBuilder {
        private Long id;
        private String logic;

        private ModuleBuilder() {
        }

        public static Module.ModuleBuilder aModule() {
            return new Module.ModuleBuilder();
        }

        public Module.ModuleBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public Module.ModuleBuilder withLogic(String logic) {
            this.logic = logic;
            return this;
        }

        public Module build() {
            Module module = new Module();
            module.setLogic(this.logic);
            module.setId(this.id);
            return module;
        }
    }
}