package c8y.mibparser.service.impl;

import c8y.mibparser.model.DeviceType;
import c8y.mibparser.model.Register;
import c8y.mibparser.model.MibUploadResult;
import c8y.mibparser.service.MibParserService;
import lombok.extern.slf4j.Slf4j;
import net.percederberg.mibble.*;
import net.percederberg.mibble.snmp.SnmpNotificationType;
import net.percederberg.mibble.snmp.SnmpTrapType;
import net.percederberg.mibble.value.ObjectIdentifierValue;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static c8y.mibparser.constants.Constants.*;
import static c8y.mibparser.utils.Misc.*;

@Slf4j
@Service
public class MibParserServiceImpl implements MibParserService {

    @Override
    public MibUploadResult processMibZipFile(MultipartFile multipartFile) throws IOException {
        MibLoader mibLoader = new MibLoader();
        String path = getTempDirectoryPath();
        File parentFile = createTempDirectory(path);

        try {
            List<Mib> mibs = extractAndLoadMainMibs(multipartFile.getInputStream(), path, mibLoader);
            return extractMibTrapInformation(mibs);
        } catch (MibLoaderException e) {
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
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            Map<String, File> fileMap = new HashMap<>();
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                if (zipEntry.isDirectory()) {
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
            throw new IllegalArgumentException(NO_MIB_FOUND_IN_ZIP_FILE);
        }
        mibLoader.addDir(file.getParentFile());
        return mibLoader.load(file);
    }

    private MibUploadResult extractMibTrapInformation(List<Mib> mibs) {
        List<Register> registers = new ArrayList<>();
        for (Mib mib: mibs) {
            registers.addAll(extractMibTrapInformation(mib));
        }
        if (CollectionUtils.isEmpty(registers)) {
            throw new IllegalArgumentException(NO_TRAPS_FOUND_IN_ZIP_FILE);
        }
        return new MibUploadResult(new DeviceType(FIELD_BUS_TYPE), registers);
    }

    private List<Register> extractMibTrapInformation(Mib mib) {
        List<Register> registerList = new ArrayList<>();
        if (mib == null) {
            return registerList;
        }

        for (MibSymbol mibSymbol : mib.getAllSymbols()) {
            if (mibSymbol instanceof MibValueSymbol) {
                MibValueSymbol mibValueSymbol = (MibValueSymbol) mibSymbol;
                if (mibValueSymbol.getType() instanceof SnmpTrapType) {
                    SnmpTrapType snmpTrapType = (SnmpTrapType) ((MibValueSymbol) mibSymbol).getType();
                    registerList.add(extractMibTrapNode(snmpTrapType, mibValueSymbol));
                } else if (mibValueSymbol.getType() instanceof SnmpNotificationType) {
                    registerList.add(extractMibTrapNode(mibValueSymbol));
                }
            }
        }
        return registerList;
    }

    private Register extractMibTrapNode(SnmpTrapType snmpTrapType, MibValueSymbol mibValueSymbol) {
        return createRegister(
                mibValueSymbol.getName(),
                ((ObjectIdentifierValue) snmpTrapType.getEnterprise()).getSymbol().getOid().toString(),
                ((ObjectIdentifierValue) snmpTrapType.getEnterprise()).getSymbol().getParent().getOid().toString(),
                ((ObjectIdentifierValue) snmpTrapType.getEnterprise()).getSymbol().getChildren(),
                ((ObjectIdentifierValue) snmpTrapType.getEnterprise()).getSymbol().getComment().replace("\n", "")
        );
    }

    private Register extractMibTrapNode(MibValueSymbol mibValueSymbol) {
        return createRegister(mibValueSymbol.getName(),
                mibValueSymbol.getOid().toString(),
                mibValueSymbol.getParent().getOid().toString(),
                mibValueSymbol.getChildren(),
                ((SnmpNotificationType) mibValueSymbol.getType()).getDescription().replace("\n", "")
        );
    }

    private Register createRegister(String name, String oid, String parentOid,
                                    MibValueSymbol[] childOid, String description) {
        List<String> childOids = new ArrayList<>();
        for (MibValueSymbol mibVS : childOid) {
            childOids.add(mibVS.getOid().toString());
        }
        return new Register(name, oid, description, parentOid, childOids);
    }

    private List<String> examineManifestFile(Map<String, File> fileMap) throws IOException {
        List<String> mainFiles;
        if (fileMap.containsKey(MANIFEST_FILENAME)) {
            mainFiles = readMainFile(fileMap.get(MANIFEST_FILENAME));
        } else {
            throw new IllegalArgumentException(NO_MANIFEST_FILE_FOUND);
        }
        if (CollectionUtils.isEmpty(mainFiles)) {
            throw new IllegalArgumentException(NO_MAIN_MIBS_FOUND);
        }
        return mainFiles;
    }
}
