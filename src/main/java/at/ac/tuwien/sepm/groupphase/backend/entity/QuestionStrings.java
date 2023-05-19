package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Embeddable;

@Embeddable
public class QuestionStrings {
    private String header;
    private String text;
    private String displayName;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
