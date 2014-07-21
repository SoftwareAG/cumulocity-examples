package com.cumulocity.tixi.server.services;

import static org.apache.commons.io.FileUtils.openOutputStream;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.copy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;

@Component
public class AgentFileSystem {

    private static final Logger logger = LoggerFactory.getLogger(AgentFileSystem.class);

    public static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    private File incomingPath;

    private File xsltProcessedPath;
    
    private File xsltPath;

    @Autowired
    public AgentFileSystem(
            @Value("${TIXI.xml.incoming}") String incomingPath,
            @Value("${TIXI.xml.xsltprocessed}") String xsltProcessedPath,
            @Value("${TIXI.xslt}") String xsltPath) {
        this.incomingPath = new File(incomingPath);
        this.xsltProcessedPath = new File(xsltProcessedPath);
        this.xsltPath = new File(xsltPath);
    }

    @PostConstruct
    public void init() {
        incomingPath.mkdirs();
        xsltProcessedPath.mkdirs();
    }
    
    public static File getFile(File parent, String fileName) {
        return new File(parent, fileName);
    }

    public String writeIncomingFile(String fileName, String requestId, InputStream inputStream) {
    	fileName = Joiner.on("_").skipNulls().join(fileName, requestId, getTimestamp()) + ".xml";
        writeToFile(inputStream, getFile(incomingPath, fileName));
        return fileName;
    }
    
    public static String getTimestamp() {
        return TIMESTAMP_FORMAT.format(new DateTime().toDate());
    }
    
    private void writeToFile(InputStream inputStream, File file) {
        FileOutputStream outputStream = null;
        try {
            outputStream = openOutputStream(file);
            copy(inputStream, outputStream);
        } catch (IOException e) {
            throw new RuntimeException("I/O error!", e);
        } finally {
            closeQuietly(outputStream);
            closeQuietly(inputStream);
        }
        logger.info("Written to file: %s.", file.getPath());
    }
    
    public File getXsltFile(Class<?> entityClass) {
    	return getFile(xsltPath, entityClass.getSimpleName() + ".xslt");
    }
    
    public File getIncomingFile(String fileName) {
    	return getFile(incomingPath, fileName);
    }
    
    public File getXsltProcessedFile(String fileName) {
    	return getFile(xsltProcessedPath, fileName);
    }
}

