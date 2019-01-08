package c8y.mibparser.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static c8y.mibparser.constants.Constants.TMPDIR;
import static c8y.mibparser.constants.Constants.TEMP_DIR_NAME;

@Slf4j
public class Misc {

    public static File createTempDirectory(String dirPath) {
        File file = new File(dirPath);
        file.mkdir();
        return file;
    }

    public static String getTempDirectoryPath() {
        return System.getProperty(TMPDIR) + File.separator + TEMP_DIR_NAME + System.currentTimeMillis();
    }

    public static List<String> readMainFile(File file) throws IOException {
        return Files.readAllLines(Paths.get(file.toURI()));
    }
}
