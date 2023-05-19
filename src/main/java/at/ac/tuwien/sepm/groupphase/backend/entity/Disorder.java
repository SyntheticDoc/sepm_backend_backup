package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.util.List;
import java.util.Map;

@Entity
@IdClass(IdVersionKey.class)
public class Disorder {
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

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<LanguageEnum.Language, DisorderStrings> text;

    public void setText(Map<LanguageEnum.Language, DisorderStrings> text) {
        this.text = text;
    }

    public Map<LanguageEnum.Language, DisorderStrings> getText() {
        return text;
    }


    @ElementCollection
    private List<Long> modules;

    public List<Long> getModules() {
        return modules;
    }

    public void setModules(List<Long> modules) {
        this.modules = modules;
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
    private Boolean deleted = false;

    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public static final class DisorderBuilder {
        private Long id;
        private String logic;

        private DisorderBuilder() {
        }

        public static Disorder.DisorderBuilder aDisorder() {
            return new Disorder.DisorderBuilder();
        }

        public Disorder.DisorderBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public Disorder.DisorderBuilder withLogic(String logic) {
            this.logic = logic;
            return this;
        }

        public Disorder build() {
            Disorder disorder = new Disorder();
            disorder.setLogic(this.logic);
            disorder.setId(this.id);
            return disorder;
        }
    }

    @Override
    public String toString() {
        return "Disorder{"
            + "id=" + id
            + ", version=" + version
            + ", text='" + text + '\''
            + ", logic='" + logic + '\''
            + '}';
    }
}
