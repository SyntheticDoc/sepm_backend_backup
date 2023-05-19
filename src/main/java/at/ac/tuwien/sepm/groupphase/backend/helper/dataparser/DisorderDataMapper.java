package at.ac.tuwien.sepm.groupphase.backend.helper.dataparser;

import at.ac.tuwien.sepm.groupphase.backend.helper.dataparser.dataclasses.ParserDisorder;
import at.ac.tuwien.sepm.groupphase.backend.helper.dataparser.dataclasses.ParserModule;
import at.ac.tuwien.sepm.groupphase.backend.helper.dataparser.dataclasses.ParserQuestion;
import at.ac.tuwien.sepm.groupphase.backend.helper.dataparser.dataclasses.ParserQuestionHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@SuppressWarnings({"DuplicatedCode", "StringBufferReplaceableByString"})
public class DisorderDataMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static ArrayList<File> jsonFiles;
    private static ArrayList<ParserDisorder> disorders;

    private static final String indentation = "  ";

    private static int fileVersion = 1;

    public void setFileLists(ArrayList<File> jsonFiles) {
        DisorderDataMapper.jsonFiles = jsonFiles;
    }

    public void setDisordersList(ArrayList<ParserDisorder> disorders) {
        DisorderDataMapper.disorders = disorders;
    }

    public void mapData() {
        for (ParserDisorder d : disorders) {
            mapDisorder(d);
        }
    }

    private void mapDisorder(ParserDisorder d) {
        StringBuilder content = new StringBuilder();

        content.append("{").append("\n");
        content.append(indentation).append("\"internalTitle\" : \"").append(cleanLine(d.internalTitle)).append("\",").append("\n");
        content.append(indentation).append("\"titleDe\" : \"").append(cleanLine(d.titleDe)).append("\",").append("\n");
        content.append(indentation).append("\"titleEn\" : \"").append(cleanLine(d.titleEn)).append("\",").append("\n");
        content.append(indentation).append("\"infodescriptionDe\" : \"").append(cleanLine(d.infodescriptionDe)).append("\",").append("\n");
        content.append(indentation).append("\"infodescriptionEn\" : \"").append(cleanLine(d.infodescriptionEn)).append("\",").append("\n");
        content.append(indentation).append("\"infoprevalenceDe\" : \"").append(cleanLine(d.infoprevalenceDe)).append("\",").append("\n");
        content.append(indentation).append("\"infoprevalenceEn\" : \"").append(cleanLine(d.infoprevalenceEn)).append("\",").append("\n");
        content.append(indentation).append("\"infocausesDe\" : \"").append(cleanLine(d.infocausesDe)).append("\",").append("\n");
        content.append(indentation).append("\"infocausesEn\" : \"").append(cleanLine(d.infocausesEn)).append("\",").append("\n");
        content.append(indentation).append("\"infosymptomsDe\" : \"").append(cleanLine(d.infosymptomsDe)).append("\",").append("\n");
        content.append(indentation).append("\"infosymptomsEn\" : \"").append(cleanLine(d.infosymptomsEn)).append("\",").append("\n");
        content.append(indentation).append("\"infocourseDe\" : \"").append(cleanLine(d.infocourseDe)).append("\",").append("\n");
        content.append(indentation).append("\"infocourseEn\" : \"").append(cleanLine(d.infocourseEn)).append("\",").append("\n");
        content.append(indentation).append("\"infotreatmentDe\" : \"").append(cleanLine(d.infotreatmentDe)).append("\",").append("\n");
        content.append(indentation).append("\"infotreatmentEn\" : \"").append(cleanLine(d.infotreatmentEn)).append("\",").append("\n");
        content.append(indentation).append("\"infolinksDe\" : \"").append(cleanLine(d.infolinksDe)).append("\",").append("\n");
        content.append(indentation).append("\"infolinksEn\" : \"").append(cleanLine(d.infolinksEn)).append("\",").append("\n");

        content.append(indentation).append("\"modules\" : [").append("\n");

        for (int i = 0; i < d.modules.size(); i++) {
            content.append(mapModule(d.modules.get(i), indentation + indentation, i == d.modules.size() - 1)).append("\n");
        }

        content.append(indentation).append("]");

        content.append("}");

        writeJson(content.toString(), getJsonByName(d));
    }

    private String mapModule(ParserModule m, String preindent, boolean isLast) {
        String indent = preindent + indentation;

        StringBuilder content = new StringBuilder();

        content.append(preindent).append("{").append("\n");
        content.append(indent).append("\"internalName\" : \"").append(m.internalName).append("\",").append("\n");

        content.append(indent).append("\"questions\" : [").append("\n");

        for (int i = 0; i < m.questions.size(); i++) {
            content.append(mapQuestion(m.questions.get(i), indent + indentation, i == m.questions.size() - 1)).append("\n");
        }

        content.append(indentation).append("]");

        content.append(preindent).append("}");

        if (!isLast) {
            content.append(",");
        }

        return content.toString();
    }

    private String mapQuestion(ParserQuestion q, String preindent, boolean isLast) {
        String indent = preindent + indentation;

        StringBuilder content = new StringBuilder();

        content.append(preindent).append("{").append("\n");
        content.append(indent).append("\"internalId\" : ").append(q.internalId).append(",").append("\n");

        // New fields which are not in the textfiles of the disorders
        content.append(indent).append("\"version\" : ").append(fileVersion).append(",").append("\n");
        content.append(indent).append("\"optional\" : ").append("false").append(",").append("\n");

        content.append(indent).append("\"internalName\" : \"").append(q.internalName).append("\",").append("\n");
        content.append(indent).append("\"testedSymptom\" : \"").append(q.testedSymptom).append("\",").append("\n");
        content.append(indent).append("\"nameDe\" : \"").append(q.nameDe).append("\",").append("\n");
        content.append(indent).append("\"nameEn\" : \"").append(q.nameEn).append("\",").append("\n");
        content.append(indent).append("\"questiontextDe\" : \"").append(cleanLine(q.questiontextDe)).append("\",").append("\n");
        content.append(indent).append("\"questiontextEn\" : \"").append(cleanLine(q.questiontextEn)).append("\",").append("\n");
        content.append(indent).append("\"answerFormat\" : \"").append(q.answerFormat).append("\",").append("\n");

        content.append(indent).append("\"QuestionHeader\" : ").append("\n");
        content.append(mapHeader(q.header, indent)).append(",").append("\n");

        // New fields which are not in the textfiles of the disorders
        content.append(indent).append("\"answer\" : ").append("\"\"").append(",").append("\n");
        content.append(indent).append("\"logic\" : ").append("\"\"").append(",").append("\n");
        content.append(indent).append("\"dependsOn\" : ").append("null").append(",").append("\n");
        content.append(indent).append("\"deleted\" : ").append("false").append("\n");

        content.append(preindent).append("}");

        if (!isLast) {
            content.append(",");
        }

        return content.toString();
    }

    private String mapHeader(ParserQuestionHeader h, String preindent) {
        String indent = preindent + indentation;

        StringBuilder content = new StringBuilder();

        content.append(preindent).append("{").append("\n");
        content.append(indent).append("\"internalTitle\" : \"").append(h.internalTitle).append("\",").append("\n");
        content.append(indent).append("\"contentDe\" : \"").append(cleanLine(h.contentDe)).append("\",").append("\n");
        content.append(indent).append("\"contentEn\" : \"").append(cleanLine(h.contentEn)).append("\"").append("\n");
        content.append(preindent).append("}");

        return content.toString();
    }

    private void writeJson(String content, File json) {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(json), StandardCharsets.UTF_8)) {
            writer.write(content);
        } catch (IOException e) {
            LOGGER.error("Could not writer to file " + json + ".", e);
        }
    }

    private String cleanLine(String s) {
        if (s == null || s.isBlank() || s.isEmpty()) {
            return s;
        }

        return s.replace("\n", "\\n").replace("\t", "  ");
    }

    private File getJsonByName(ParserDisorder disorder) {
        for (File f : jsonFiles) {
            if (f.getName().equals(disorder.internalTitle.replace(" ", "_").replace("/", "-") + ".json")) {
                return f;
            }
        }

        return null;
    }

    private void logThis(String s) {
        LOGGER.info("\n\n\n################################################\n" + s
            + "\n################################################\n\n");
    }
}
