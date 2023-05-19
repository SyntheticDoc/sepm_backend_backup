package at.ac.tuwien.sepm.groupphase.backend.helper.dataparser.dataclasses;

public class ParserQuestionHeader {
    public String internalTitle;
    public String contentDe;
    public String contentEn;

    @Override
    public String toString() {
        return "ParserQuestionHeader{"
            + "internalTitle='" + internalTitle + '\''
            + ", contentDe='" + contentDe + '\''
            + ", contentEn='" + contentEn + '\''
            + '}';
    }
}
