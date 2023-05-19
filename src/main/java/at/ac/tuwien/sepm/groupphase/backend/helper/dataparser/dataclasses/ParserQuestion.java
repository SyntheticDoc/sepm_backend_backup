package at.ac.tuwien.sepm.groupphase.backend.helper.dataparser.dataclasses;

public class ParserQuestion {
    public int internalId;
    public String internalName;
    public String testedSymptom;
    public String nameDe;
    public String nameEn;
    public String questiontextDe;
    public String questiontextEn;
    public String answerFormat;

    public ParserQuestionHeader header;

    @Override
    public String toString() {
        return "ParserQuestion{"
            + "internalId=" + internalId
            + ", internalName='" + internalName + '\''
            + ", testedSymptom='" + testedSymptom + '\''
            + ", nameDe='" + nameDe + '\''
            + ", nameEn='" + nameEn + '\''
            + ", questiontextDe='" + questiontextDe + '\''
            + ", questiontextEn='" + questiontextEn + '\''
            + ", answerFormat='" + answerFormat + '\''
            + ", header=" + header
            + '}';
    }
}
