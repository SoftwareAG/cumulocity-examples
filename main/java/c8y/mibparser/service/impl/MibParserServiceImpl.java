package c8y.mibparser.service.impl;

import c8y.mibparser.customexception.IllegalMibUploadException;
import c8y.mibparser.model.DeviceType;
import c8y.mibparser.model.Register;
import c8y.mibparser.model.Root;
import c8y.mibparser.service.MibParserService;
import net.percederberg.mibble.*;
import net.percederberg.mibble.snmp.SnmpNotificationType;
import net.percederberg.mibble.snmp.SnmpTrapType;
import net.percederberg.mibble.value.ObjectIdentifierValue;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static c8y.mibparser.constants.PlaceHolders.*;
import static c8y.mibparser.utils.Misc.*;

@Component
public class MibParserServiceImpl implements MibParserService {

    private MibLoader mibLoader;

    @Override
    public String processMibZipFile(MultipartFile file) throws IOException, IllegalMibUploadException {
        mibLoader = new MibLoader();
        String path = getDirectoryPath();
        File parentFile = createTempDirectory(path);

        try {
            List<Mib> mibs = extractAndLoadMainMibs(file.getInputStream(), path);
            return getMibJson(extractMibTrapInformation(mibs));
        } catch (MibLoaderException e) {
            throw new IllegalMibUploadException(MISSING_MIB_DEPENDENCIES);
        } finally {
            FileUtils.deleteDirectory(parentFile);
            mibLoader.unloadAll();
            mibLoader.removeAllDirs();
        }
    }

    private List<Mib> extractAndLoadMainMibs(InputStream inputStream, String dirPath)
            throws MibLoaderException, IOException, IllegalMibUploadException {
        ZipInputStream zipInputStream = null;
        List<Mib> mibs = new ArrayList<>();
        try {
            ZipEntry zipEntry;
            Map<String, File> fileMap = new HashMap<>();
            zipInputStream = new ZipInputStream(inputStream);
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.isDirectory()) {
                    throw new IllegalMibUploadException(DIR_NOT_ALLOWED);
                }
                File mibfile = new File(dirPath + File.separator + zipEntry.getName());
                FileOutputStream fos = new FileOutputStream(mibfile);
                byte[] bytes = new byte[(int) zipEntry.getSize()];
                int length;
                while ((length = zipInputStream.read(bytes)) >= 0) {
                    fos.write(bytes, 0, length);
                }
                fileMap.put(mibfile.getName(), mibfile);
                fos.close();
                zipInputStream.closeEntry();
            }
            List<String> mainFiles = examineManifestFile(fileMap);
            for (String mainFileName : mainFiles) {
                mibs.add(loadMib(fileMap.get(mainFileName)));
            }
            return mibs;
        } finally {
            closeInputStream(zipInputStream);
        }
    }

    private Mib loadMib(File file) throws IOException, MibLoaderException {
        mibLoader.addDir(file.getParentFile());
        return mibLoader.load(file);
    }

    private Root extractMibTrapInformation(List<Mib> mibs) {
        List<Register> registers = new ArrayList<>();
        for (Mib mib: mibs) {
            registers.addAll(extractMibTrapInformation(mib));
        }
        return  new Root(new DeviceType(FIELD_BUS_TYPE), registers);
    }

    private List<Register> extractMibTrapInformation(Mib mib) {
        List<Register> registerList = new ArrayList<>();
        if (mib == null)
            return registerList;

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

    private Register extractMibTrapNode(MibValueSymbol mibValueSymbol) {
        return createRegister(mibValueSymbol.getName(),
                mibValueSymbol.getOid().toString(),
                mibValueSymbol.getParent().getOid().toString(),
                mibValueSymbol.getChildren(),
                ((SnmpNotificationType) mibValueSymbol.getType()).getDescription().replace("\n", "")
        );
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

    private Register createRegister(String name, String oid, String parentOid, MibValueSymbol[] childOid, String description) {
        List<String> childOids = new ArrayList<>();
        for (MibValueSymbol mibVS : childOid) {
            childOids.add(mibVS.getOid().toString());
        }
        return new Register(name, oid, description, parentOid, childOids);
    }

    private List<String> examineManifestFile(Map<String, File> fileMap)
            throws IllegalMibUploadException, IOException {
        List<String> mainFiles = new ArrayList<>();
        if (fileMap.containsKey(MANIFEST_FILENAME)) {
            mainFiles = readMainFile(fileMap.get(MANIFEST_FILENAME));
        }
        if (CollectionUtils.isEmpty(mainFiles))
            throw new IllegalMibUploadException(NO_MANIFEST_FILE_FOUND);
        return mainFiles;
    }
}
