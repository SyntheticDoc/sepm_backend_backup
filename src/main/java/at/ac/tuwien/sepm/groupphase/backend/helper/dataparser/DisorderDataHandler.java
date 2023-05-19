package at.ac.tuwien.sepm.groupphase.backend.helper.dataparser;

import at.ac.tuwien.sepm.groupphase.backend.helper.dataparser.dataclasses.ParserDisorder;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Stream;

/* Run with: mvn spring-boot:run -Dspring-boot.run.arguments="##ARGUMENT##"

   Replace ##ARGUMENT## with parse-data_replace to automatically delete ALL Files in src\main\resources\json\disorderdata
   and parse the text files again

   Replace ##ARGUMENT## with parse-data_skip to abort parsing if json files already exist in src\main\resources\json\disorderdata
 */
@Component
public class DisorderDataHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static String rootPath;
    private static final String JSONPath_string = "\\src\\main\\resources\\json\\disorderdata\\";
    private static final String backupPath_string = "\\src\\main\\resources\\json\\backup\\disorderdata\\";
    private static final String textPath_string = "\\src\\main\\resources\\text\\disorderdata\\";

    private static File JSONPath;
    private static File backupPath;
    private static File textPath;

    private static ArrayList<File> textFiles;
    private static ArrayList<File> jsonFiles = new ArrayList<>();

    private static DisorderDataParser parser;
    private static DisorderDataMapper mapper;

    @Autowired
    public DisorderDataHandler(DisorderDataParser parser, DisorderDataMapper mapper) {
        DisorderDataHandler.parser = parser;
        DisorderDataHandler.mapper = mapper;
    }

    public static void init(boolean replace) {
        LOGGER.info("Starting DisorderDataHandler");
        rootPath = System.getProperty("user.dir");

        JSONPath = new File(rootPath + JSONPath_string);
        backupPath = new File(rootPath + backupPath_string);
        textPath = new File(rootPath + textPath_string);

        // Create necessary files
        if (replace && !isEmptyDir(JSONPath.toPath())) {
            try {
                LOGGER.info("Backing up files in directory " + JSONPath.toString() + " to " + backupPath.toString());
                FileUtils.cleanDirectory(backupPath);
                FileUtils.copyDirectory(JSONPath, backupPath);
                LOGGER.info("Removing all files in directory " + JSONPath.toString());
                FileUtils.cleanDirectory(JSONPath);
                enumerateTextFiles();
                createJsons(false);
            } catch (IOException e) {
                LOGGER.error("Can't clean directory " + JSONPath.toString() + ", aborting parsing." + e);
            }
        } else {
            enumerateTextFiles();
            enumerateJsonFiles();
            createJsons(true);
        }

        // Start parsing and mapping data
        parser.setFileLists(textFiles);
        ArrayList<ParserDisorder> disorder = parser.parseData();
        mapper.setFileLists(jsonFiles);
        mapper.setDisordersList(disorder);
        mapper.mapData();
        LOGGER.info("All files successfully parsed and mapped!");
    }

    private static void enumerateTextFiles() {
        textFiles = new ArrayList<>(FileUtils.listFiles(textPath, new String[]{"txt"}, true));
    }

    private static void enumerateJsonFiles() {
        jsonFiles = new ArrayList<>(FileUtils.listFiles(JSONPath, new String[]{"json"}, true));
    }

    private static void createJsons(boolean skipExisting) {
        String filename = "";
        Scanner sc;

        for (File f : textFiles) {
            try {
                sc = new Scanner(f);

                if (sc.hasNextLine()) {
                    filename = sc.nextLine().replace(" ", "_").replace("/", "-");
                } else {
                    LOGGER.error("File " + f + " was empty, but it was expected to contain data.");
                }
            } catch (FileNotFoundException e) {
                LOGGER.error("Could not find text file " + f, e);
            }

            if (skipExisting && !listContainsFile(jsonFiles, filename + ".json")) {
                jsonFiles.add(createjsonFile(filename));
            } else if (!skipExisting) {
                jsonFiles.add(createjsonFile(filename));
            }
        }
    }

    private static File createjsonFile(String filename) {
        File file = new File(JSONPath + "\\" + filename + ".json");

        try {
            if (!file.createNewFile()) {
                throw new IllegalStateException("Could not create file " + file + "!");
            }
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
        }

        return file;
    }

    private static boolean listContainsFile(ArrayList<File> list, String filename) {
        for (File f : list) {
            if (f.getName().equals(filename)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isEmptyDir(Path path) {
        if (Files.isDirectory(path)) {
            try (Stream<Path> files = Files.list(path)) {
                return files.findFirst().isEmpty();
            } catch (IOException e) {
                LOGGER.error("Error while trying to analyze directory " + path + "!", e);
            }
        }

        return false;
    }
}
