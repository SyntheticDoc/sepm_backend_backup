package at.ac.tuwien.sepm.groupphase.backend.helper.dataparser.dataclasses;

import java.util.ArrayList;

public class ParserDisorder {
    public String internalTitle;
    public String titleDe;
    public String titleEn;
    public String infodescriptionDe;
    public String infodescriptionEn;
    public String infoprevalenceDe;
    public String infoprevalenceEn;
    public String infocausesDe;
    public String infocausesEn;
    public String infosymptomsDe;
    public String infosymptomsEn;
    public String infocourseDe;
    public String infocourseEn;
    public String infotreatmentDe;
    public String infotreatmentEn;
    public String infolinksDe;
    public String infolinksEn;

    public ArrayList<ParserModule> modules;

    @Override
    public String toString() {
        return "ParserDisorder{"
            + "internalTitle='" + internalTitle + '\''
            + ", titleDe='" + titleDe + '\''
            + ", titleEn='" + titleEn + '\''
            + ", infodescriptionDe='" + infodescriptionDe + '\''
            + ", infodescriptionEn='" + infodescriptionEn + '\''
            + ", infoprevalenceDe='" + infoprevalenceDe + '\''
            + ", infoprevalenceEn='" + infoprevalenceEn + '\''
            + ", infocausesDe='" + infocausesDe + '\''
            + ", infocausesEn='" + infocausesEn + '\''
            + ", infosymptomsDe='" + infosymptomsDe + '\''
            + ", infosymptomsEn='" + infosymptomsEn + '\''
            + ", infocourseDe='" + infocourseDe + '\''
            + ", infocourseEn='" + infocourseEn + '\''
            + ", infotreatmentDe='" + infotreatmentDe + '\''
            + ", infotreatmentEn='" + infotreatmentEn + '\''
            + ", infolinksDe='" + infolinksDe + '\''
            + ", infolinksEn='" + infolinksEn + '\''
            + ", modules=" + modules
            + '}';
    }
}
