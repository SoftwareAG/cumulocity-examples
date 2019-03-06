package com.cumulocity.mibparser.service.impl;

import com.cumulocity.mibparser.conversion.RegisterConversionHandler;
import com.cumulocity.mibparser.model.DeviceType;
import com.cumulocity.mibparser.model.Register;
import com.cumulocity.mibparser.model.MibUploadResult;
import com.cumulocity.mibparser.service.MibParserService;
import lombok.extern.slf4j.Slf4j;
import net.percederberg.mibble.*;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.cumulocity.mibparser.constants.Constants.*;
import static com.cumulocity.mibparser.utils.MibParserUtil.*;

@Slf4j
@Service
public class MibParserServiceImpl implements MibParserService {

    @Autowired
    private RegisterConversionHandler handler;

    @Override
    public MibUploadResult processMibZipFile(MultipartFile multipartFile) throws IOException {
        MibLoader mibLoader = new MibLoader();
        String path = getTempDirectoryPath();
        File parentFile = createTempDirectory(path);

        try {
            List<Mib> mibs = extractAndLoadMainMibs(multipartFile.getInputStream(), path, mibLoader);
            return extractMibInformation(mibs);
        } catch (MibLoaderException e) {
            log.error(MISSING_MIB_DEPENDENCIES);
            throw new IllegalArgumentException(MISSING_MIB_DEPENDENCIES);
        } finally {
            FileUtils.deleteDirectory(parentFile);
            mibLoader.unloadAll();
            mibLoader.removeAllDirs();
        }
    }

    private List<Mib> extractAndLoadMainMibs(InputStream inputStream, String tmpDirPath, MibLoader mibLoader)
            throws MibLoaderException, IOException {
        List<Mib> mibs = new ArrayList<>();
        log.info("Extracting all files from Zip");
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            Map<String, File> fileMap = new HashMap<>();
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                if (zipEntry.isDirectory()) {
                    log.error(DIR_NOT_ALLOWED);
                    throw new IllegalArgumentException(DIR_NOT_ALLOWED);
                }
                File mibfile = new File(tmpDirPath + File.separator + zipEntry.getName());
                try (FileOutputStream fos = new FileOutputStream(mibfile)) {
                    byte[] bytes = new byte[(int) zipEntry.getSize()];
                    int length;
                    while ((length = zipInputStream.read(bytes)) > 0) {
                        fos.write(bytes, 0, length);
                    }
                    fileMap.put(mibfile.getName(), mibfile);
                }
                zipInputStream.closeEntry();
                zipEntry = zipInputStream.getNextEntry();
            }
            List<String> mainFiles = examineManifestFile(fileMap);
            for (String mainFileName : mainFiles) {
                mibs.add(loadMib(fileMap.get(mainFileName), mibLoader));
            }
            return mibs;
        }
    }

    private Mib loadMib(File file, MibLoader mibLoader) throws IOException, MibLoaderException {
        if (file == null) {
            log.error(NO_MIB_FOUND_IN_ZIP_FILE);
            throw new IllegalArgumentException(NO_MIB_FOUND_IN_ZIP_FILE);
        }
        log.debug("Loading main MIB file " + file.getName());
        mibLoader.addDir(file.getParentFile());
        return mibLoader.load(file);
    }

    private MibUploadResult extractMibInformation(List<Mib> mibs) {
        log.debug("Extracting MIB information");
        List<Register> registers = new ArrayList<>();
        for (Mib mib : mibs) {
            registers.addAll(extractMibInformation(mib));
        }
        if (CollectionUtils.isEmpty(registers)) {
            log.error(NO_MIB_INFO_FOUND_IN_ZIP_FILE);
            throw new IllegalArgumentException(NO_MIB_INFO_FOUND_IN_ZIP_FILE);
        }
        log.info("MIB Zip file processed");
        return new MibUploadResult(new DeviceType(FIELD_BUS_TYPE), registers);
    }

    private List<Register> extractMibInformation(Mib mib) {
        log.debug("Processing each MIB component of " + mib.getName());
        if (mib == null) {
            return Collections.EMPTY_LIST;
        }
        return handler.convertSnmpObjectToRegister(mib.getAllSymbols());
    }

    private List<String> examineManifestFile(Map<String, File> fileMap) throws IOException {
        log.info("Examining all MIB files");
        List<String> mainFiles;
        if (fileMap.containsKey(MANIFEST_FILENAME)) {
            mainFiles = readMainFile(fileMap.get(MANIFEST_FILENAME));
        } else {
            log.error(NO_MANIFEST_FILE_FOUND);
            throw new IllegalArgumentException(NO_MANIFEST_FILE_FOUND);
        }
        if (CollectionUtils.isEmpty(mainFiles)) {
            log.error(NO_MAIN_MIBS_FOUND);
            throw new IllegalArgumentException(NO_MAIN_MIBS_FOUND);
        }
        return mainFiles;
    }
}
