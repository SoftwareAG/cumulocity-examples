package com.cumulocity.mibparser.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.cumulocity.mibparser.constants.Constants.TMPDIR;
import static com.cumulocity.mibparser.constants.Constants.TEMP_DIR_NAME;

public class MibParserUtil {

    public static File createTempDirectory(String dirPath) {
        File file = new File(dirPath);
        file.mkdir();
        return file;
    }

    public static String getTempDirectoryPath() {
        return System.getProperty(TMPDIR) + File.separator + TEMP_DIR_NAME + System.currentTimeMillis();
    }

    public static List<String> readMainFile(File file) throws IOException {
        return Files.readAllLines(Paths.get(file.toURI()), StandardCharsets.UTF_8);
    }
}
