package at.ac.tuwien.sepm.groupphase.backend.helper.dataparser;

import at.ac.tuwien.sepm.groupphase.backend.helper.dataparser.dataclasses.ParserDisorder;
import at.ac.tuwien.sepm.groupphase.backend.helper.dataparser.dataclasses.ParserModule;
import at.ac.tuwien.sepm.groupphase.backend.helper.dataparser.dataclasses.ParserQuestion;
import at.ac.tuwien.sepm.groupphase.backend.helper.dataparser.dataclasses.ParserQuestionHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class DisorderDataParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static ArrayList<File> textFiles;

    private static final ArrayList<ParserDisorder> disorders = new ArrayList<>();

    public void setFileLists(ArrayList<File> textFiles) {
        DisorderDataParser.textFiles = textFiles;
    }

    public ArrayList<ParserDisorder> parseData() {
        for (File f : textFiles) {
            parseFile(f);
        }

        if (!disorders.isEmpty()) {
            return disorders;
        } else {
            LOGGER.error("Could not parse disorders, disorder list was empty!");
            return null;
        }
    }

    public void parseFile(File textfile) {
        Scanner sc;
        ParserDisorder disorder = new ParserDisorder();

        // Try to open the file
        try {
            sc = new Scanner(textfile, StandardCharsets.UTF_8.name());
        } catch (FileNotFoundException e) {
            LOGGER.error("Could not find file " + textfile + ".", e);
            return;
        }

        if (!sc.hasNextLine()) {
            LOGGER.error("File " + textfile + " seemed to be empty, skipping file");
            return;
        }

        disorder.internalTitle = sc.nextLine();

        String line;
        StringBuilder temp;
        ParserQuestionHeader currentQuestionHeader = null;
        ArrayList<ParserModule> modules = new ArrayList<>();

        // Create necessary modules and connect modules to current disorder
        modules.add(new ParserModule());
        modules.get(0).internalName = "A";
        disorder.modules = modules;

        LOGGER.trace("Starting parser with: " + textfile.getAbsolutePath());

        while (sc.hasNextLine()) {
            line = sc.nextLine();

            if (line.contains("Diagnosetitel, deutsch:")) {
                disorder.titleDe = line.split(": ")[1];
                continue;
            }

            if (line.contains("Diagnosetitel, englisch:")) {
                disorder.titleEn = line.split(": ")[1];
                continue;
            }

            // parse german infotext
            if (line.contains("INFOTEXT DEUTSCH ANFANG")) {
                // startup flag to parse beginning descriptive text
                boolean startup = true;
                temp = new StringBuilder();

                while (sc.hasNextLine()) {
                    line = sc.nextLine();
                    //LOGGER.info("Reading line INNER GERMAN LOOP: " + line);

                    if (startup) {
                        if (line.trim().isBlank()) {
                            startup = false;
                            continue;
                        }

                        if (!temp.isEmpty()) {
                            temp.append("\n");
                        }

                        temp.append(line);

                        disorder.infodescriptionDe = temp.toString();
                        continue;
                    }

                    if (line.contains("Häufigkeit:")) {
                        temp = new StringBuilder();

                        while (sc.hasNextLine()) {
                            line = sc.nextLine();

                            if (line.trim().isBlank()) {
                                break;
                            }

                            if (!temp.isEmpty()) {
                                temp.append("\n");
                            }

                            temp.append(line);
                        }

                        disorder.infoprevalenceDe = temp.toString();
                        continue;
                    }

                    if (line.contains("Ursachen:")) {
                        temp = new StringBuilder();

                        while (sc.hasNextLine()) {
                            line = sc.nextLine();

                            if (line.trim().isBlank()) {
                                break;
                            }

                            if (!temp.isEmpty()) {
                                temp.append("\n");
                            }

                            temp.append(line);
                        }

                        disorder.infocausesDe = temp.toString();
                        continue;
                    }

                    if (line.contains("Symptome:")) {
                        temp = new StringBuilder();

                        while (sc.hasNextLine()) {
                            line = sc.nextLine();

                            if (line.trim().isBlank()) {
                                break;
                            }

                            if (!temp.isEmpty()) {
                                temp.append("\n");
                            }

                            temp.append(line);
                        }

                        disorder.infosymptomsDe = temp.toString();
                        continue;
                    }

                    if (line.contains("Verlauf:")) {
                        temp = new StringBuilder();

                        while (sc.hasNextLine()) {
                            line = sc.nextLine();

                            if (line.trim().isBlank()) {
                                break;
                            }

                            if (!temp.isEmpty()) {
                                temp.append("\n");
                            }

                            temp.append(line);
                        }

                        disorder.infocourseDe = temp.toString();
                        continue;
                    }

                    if (line.contains("Behandlung:")) {
                        temp = new StringBuilder();

                        while (sc.hasNextLine()) {
                            line = sc.nextLine();

                            if (line.trim().isBlank()) {
                                break;
                            }

                            if (!temp.isEmpty()) {
                                temp.append("\n");
                            }

                            temp.append(line);
                        }

                        disorder.infotreatmentDe = temp.toString();
                        continue;
                    }

                    if (line.contains("Weiterführende Informationen:")) {
                        temp = new StringBuilder();

                        while (sc.hasNextLine()) {
                            line = sc.nextLine();

                            if (line.trim().isBlank()) {
                                break;
                            }

                            if (!temp.isEmpty()) {
                                temp.append("\n");
                            }

                            temp.append(line);
                        }

                        disorder.infolinksDe = temp.toString();
                        continue;
                    }

                    if (line.contains("INFOTEXT DEUTSCH ENDE")) {
                        break;
                    }
                }

                continue;
            }

            // parse english infotext
            if (line.contains("INFOTEXT ENGLISCH ANFANG")) {
                // startup flag to parse beginning descriptive text
                boolean startup = true;
                temp = new StringBuilder();

                while (sc.hasNextLine()) {
                    line = sc.nextLine();
                    // LOGGER.info("Reading line INNER ENGLISCH LOOP: " + line);

                    if (startup) {
                        if (line.trim().isBlank()) {
                            startup = false;
                            continue;
                        }

                        if (!temp.isEmpty()) {
                            temp.append("\n");
                        }

                        temp.append(line);

                        disorder.infodescriptionEn = temp.toString();
                        continue;
                    }

                    if (line.contains("Prevalence:")) {
                        temp = new StringBuilder();

                        while (sc.hasNextLine()) {
                            line = sc.nextLine();

                            if (line.trim().isBlank()) {
                                break;
                            }

                            if (!temp.isEmpty()) {
                                temp.append("\n");
                            }

                            temp.append(line);
                        }

                        disorder.infoprevalenceEn = temp.toString();
                        continue;
                    }

                    if (line.contains("Causes:")) {
                        temp = new StringBuilder();

                        while (sc.hasNextLine()) {
                            line = sc.nextLine();

                            if (line.trim().isBlank()) {
                                break;
                            }

                            if (!temp.isEmpty()) {
                                temp.append("\n");
                            }

                            temp.append(line);
                        }

                        disorder.infocausesEn = temp.toString();
                        continue;
                    }

                    if (line.contains("Symptoms:")) {
                        temp = new StringBuilder();

                        while (sc.hasNextLine()) {
                            line = sc.nextLine();

                            if (line.trim().isBlank()) {
                                break;
                            }

                            if (!temp.isEmpty()) {
                                temp.append("\n");
                            }

                            temp.append(line);
                        }

                        disorder.infosymptomsEn = temp.toString();
                        continue;
                    }

                    if (line.contains("Course:")) {
                        temp = new StringBuilder();

                        while (sc.hasNextLine()) {
                            line = sc.nextLine();

                            if (line.trim().isBlank()) {
                                break;
                            }

                            if (!temp.isEmpty()) {
                                temp.append("\n");
                            }

                            temp.append(line);
                        }

                        disorder.infocourseEn = temp.toString();
                        continue;
                    }

                    if (line.contains("Treatment:")) {
                        temp = new StringBuilder();

                        while (sc.hasNextLine()) {
                            line = sc.nextLine();

                            if (line.trim().isBlank()) {
                                break;
                            }

                            if (!temp.isEmpty()) {
                                temp.append("\n");
                            }

                            temp.append(line);
                        }

                        disorder.infotreatmentEn = temp.toString();
                        continue;
                    }

                    if (line.contains("Additional information:")) {
                        temp = new StringBuilder();

                        while (sc.hasNextLine()) {
                            line = sc.nextLine();

                            if (line.trim().isBlank()) {
                                break;
                            }

                            if (!temp.isEmpty()) {
                                temp.append("\n");
                            }

                            temp.append(line);
                        }

                        disorder.infolinksEn = temp.toString();
                        continue;
                    }

                    if (line.contains("INFOTEXT ENGLISCH ENDE")) {
                        break;
                    }
                }

                continue;
            }

            if (line.contains("Einleitungssatz am Anfang des Fragebogens, deutsch:")) {
                currentQuestionHeader = new ParserQuestionHeader();
                currentQuestionHeader.contentDe = line.split("deutsch: ")[1];

                while (sc.hasNextLine()) {
                    line = sc.nextLine();

                    if (line.trim().isBlank() || line.startsWith("Einleitungssatz am Anfang")) {
                        break;
                    }

                    currentQuestionHeader.contentDe += "\n";
                    currentQuestionHeader.contentDe += line;
                }

                modules.get(modules.size() - 1).questions = new ArrayList<>();
            }

            if (line.contains("Einleitungssatz am Anfang des Fragebogens, english:")) {
                currentQuestionHeader.contentEn = line.split("english: ")[1];

                while (sc.hasNextLine()) {
                    line = sc.nextLine();

                    if (line.trim().isBlank() || line.startsWith("ID")) {
                        break;
                    }

                    currentQuestionHeader.contentEn += "\n";
                    currentQuestionHeader.contentEn += line;
                }
            }

            if (line.contains("Weiterer Fragebogentext, deutsch:")
                || line.contains("Einleitungssatz am Anfang des Fragebogens, deutsch:")
                || line.contains("Einleitender Satz vor nächstem Fragensatz, deutsch:")
                || line.contains("Fragen-Begleittext ab hier, deutsch:")) {
                currentQuestionHeader = new ParserQuestionHeader();
                currentQuestionHeader.contentDe = line.split("deutsch: ")[1];

                while (sc.hasNextLine()) {
                    line = sc.nextLine();

                    if (line.trim().isBlank() || line.startsWith("ID")) {
                        break;
                    }

                    currentQuestionHeader.contentDe += "\n";
                    currentQuestionHeader.contentDe += line;
                }
            }

            if (line.contains("Weiterer Fragebogentext, english:")
                || line.contains("Einleitungssatz am Anfang des Fragebogens, english:")
                || line.contains("Einleitender Satz vor nächstem Fragensatz, english:")
                || line.contains("Fragen-Begleittext ab hier, english:")) {
                currentQuestionHeader.contentEn = line.split("english: ")[1];

                while (sc.hasNextLine()) {
                    line = sc.nextLine();

                    if (line.trim().isBlank() || line.startsWith("ID")) {
                        break;
                    }

                    currentQuestionHeader.contentEn += "\n";
                    currentQuestionHeader.contentEn += line;
                }
            }

            if (line.contains("ID")) {
                // Get question id
                int questionId = Integer.parseInt(line.split("\t")[1]);;

                getModule(modules, questionId, textfile);

                ParserModule currentModule = modules.get(modules.size() - 1);

                // Prepare new question object
                currentModule.questions.add(new ParserQuestion());
                ParserQuestion currentQuestion = currentModule.questions.get(currentModule.questions.size() - 1);

                currentQuestion.header = currentQuestionHeader;
                currentQuestion.internalId = questionId;

                while (sc.hasNextLine()) {
                    line = sc.nextLine();

                    if (line.trim().isBlank()) {
                        break;
                    }

                    if (line.startsWith("Interner Name")) {
                        currentQuestion.internalName = line.split("\t")[1];
                        continue;
                    }

                    if (line.startsWith("Geprüftes Symptom")) {
                        currentQuestion.testedSymptom = line.split("\t")[1];
                        continue;
                    }

                    if (line.startsWith("Symptomname, Deutsch")) {
                        currentQuestion.nameDe = line.split("\t")[1];
                        continue;
                    }

                    if (line.startsWith("Symptomname, Englisch")) {
                        currentQuestion.nameEn = line.split("\t")[1];
                        continue;
                    }

                    if (line.startsWith("Fragentext, Deutsch")) {
                        currentQuestion.questiontextDe = line.split("\t")[1];
                        continue;
                    }

                    if (line.startsWith("Fragentext, Englisch")) {
                        currentQuestion.questiontextEn = line.split("\t")[1];
                        continue;
                    }

                    if (line.startsWith("Antwortformat")) {
                        currentQuestion.answerFormat = line.split("\t")[1];
                        continue;
                    }
                }
            }

            if (line.contains("END OF FILE")) {
                disorders.add(disorder);
                break;
            }
        }
    }

    public void getModule(ArrayList<ParserModule> modules, int questionId, File textfile) {
        // Get correct module depending on disorder
        if (isAutism(textfile)) {
            if (questionId == 11) {
                ParserModule moduleB = new ParserModule("B");
                moduleB.questions = new ArrayList<>();
                modules.add(moduleB);
            } else if (questionId == 24) {
                ParserModule moduleC = new ParserModule("C");
                moduleC.questions = new ArrayList<>();
                modules.add(moduleC);
            } else if (questionId == 25) {
                ParserModule moduleD = new ParserModule("D");
                moduleD.questions = new ArrayList<>();
                modules.add(moduleD);
            } else if (questionId == 26) {
                ParserModule moduleD = new ParserModule("D");
                moduleD.questions = new ArrayList<>();
                modules.add(moduleD);
            }
        } else if (isAdhd(textfile)) {
            if (questionId == 12) {
                ParserModule moduleB = new ParserModule("B");
                moduleB.questions = new ArrayList<>();
                modules.add(moduleB);
            }
        } else if (isSchizophrenia(textfile)) {
            if (questionId == 15) {
                ParserModule moduleB = new ParserModule("B");
                moduleB.questions = new ArrayList<>();
                modules.add(moduleB);
            }
        } else if (isOcd(textfile)) {
            if (questionId == 4) {
                ParserModule moduleB = new ParserModule("B");
                moduleB.questions = new ArrayList<>();
                modules.add(moduleB);
            } else if (questionId == 6) {
                ParserModule moduleC = new ParserModule("C");
                moduleC.questions = new ArrayList<>();
                modules.add(moduleC);
            }
        } else if (isPtsd(textfile)) {
            if (questionId == 4) {
                ParserModule moduleB = new ParserModule("B");
                moduleB.questions = new ArrayList<>();
                modules.add(moduleB);
            } else if (questionId == 9) {
                ParserModule moduleC = new ParserModule("C");
                moduleC.questions = new ArrayList<>();
                modules.add(moduleC);
            } else if (questionId == 11) {
                ParserModule moduleD = new ParserModule("D");
                moduleD.questions = new ArrayList<>();
                modules.add(moduleD);
            } else if (questionId == 18) {
                ParserModule moduleE = new ParserModule("E");
                moduleE.questions = new ArrayList<>();
                modules.add(moduleE);
            } else if (questionId == 24) {
                ParserModule moduleF = new ParserModule("F");
                moduleF.questions = new ArrayList<>();
                modules.add(moduleF);
            } else if (questionId == 25) {
                ParserModule moduleG = new ParserModule("G");
                moduleG.questions = new ArrayList<>();
                modules.add(moduleG);
            }
        } else if (isAnorexia(textfile)) {
            if (questionId == 1) {
                ParserModule moduleB = new ParserModule("B");
                moduleB.questions = new ArrayList<>();
                modules.add(moduleB);
            } else if (questionId == 2) {
                ParserModule moduleC = new ParserModule("C");
                moduleC.questions = new ArrayList<>();
                modules.add(moduleC);
            } else if (questionId == 4) {
                ParserModule moduleD = new ParserModule("D");
                moduleD.questions = new ArrayList<>();
                modules.add(moduleD);
            }
        } else if (isAntisocial(textfile)) {
            if (questionId == 7) {
                ParserModule moduleB = new ParserModule("B");
                moduleB.questions = new ArrayList<>();
                modules.add(moduleB);
            } else if (questionId == 8) {
                ParserModule moduleC = new ParserModule("C");
                moduleC.questions = new ArrayList<>();
                modules.add(moduleC);
            } else if (questionId == 9) {
                ParserModule moduleD = new ParserModule("D");
                moduleD.questions = new ArrayList<>();
                modules.add(moduleD);
            }
        }
    }

    public boolean isAutism(File f) {
        return f.getName().equals("2.txt");
    }

    public boolean isAdhd(File f) {
        return f.getName().equals("3.txt");
    }

    public boolean isSchizophrenia(File f) {
        return f.getName().equals("4.txt");
    }

    public boolean isOcd(File f) {
        return f.getName().equals("6.txt");
    }

    public boolean isPtsd(File f) {
        return f.getName().equals("7.txt");
    }

    public boolean isAnorexia(File f) {
        return f.getName().equals("8.txt");
    }

    public boolean isAntisocial(File f) {
        return f.getName().equals("9.txt");
    }

    private void logThis(String s) {
        LOGGER.info("\n\n\n################################################\n\t\t" + s
            + "\n################################################\n\n");
    }
}
