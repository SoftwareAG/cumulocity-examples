package c8y.mibparser.utils;

import c8y.mibparser.model.Root;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static c8y.mibparser.constants.PlaceHolders.HOME_DIR;
import static c8y.mibparser.constants.PlaceHolders.TEMP_DIR_NAME;

public class Misc {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String getMibJson(Root root) throws JsonProcessingException {
        return objectMapper.writeValueAsString(root);
    }

    public static File createTempDirectory(String dirPath) {
        File file = new File(dirPath);
        file.mkdir();
        return file;
    }

    public static String getDirectoryPath() {
        return System.getProperty(HOME_DIR) + File.separator + TEMP_DIR_NAME + System.currentTimeMillis();
    }

    public static void closeInputStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return;
        }
        inputStream.close();
    }

    public static List<String> readMainFile(File file) throws IOException {
        return Files.readAllLines(Paths.get(file.toURI()));
    }
}
