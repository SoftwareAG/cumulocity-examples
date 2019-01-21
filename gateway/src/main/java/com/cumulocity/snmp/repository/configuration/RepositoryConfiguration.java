package com.cumulocity.snmp.repository.configuration;

import com.cumulocity.snmp.platform.PlatformObjectMapperConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class RepositoryConfiguration {

    public static File findConfSubdirectory(String subfolderName) {
        final File home = new File(System.getProperty("user.home"), "snmp");
        final File etc = new File("/etc/snmp");
        final File confDirectory;
        if (home.exists()) {
            confDirectory = home;
        } else if (etc.exists()) {
            confDirectory = etc;
        } else {
            confDirectory = new File(System.getProperty("java.io.tmpdir"));
        }

        final File dbDirectory = new File(confDirectory, subfolderName);
        if (!dbDirectory.exists()) {
            dbDirectory.mkdir();
        }
        return dbDirectory;
    }

    @Bean
    public static ObjectMapper objectMapper() {
        final ObjectMapper result = new ObjectMapper();
        PlatformObjectMapperConfiguration.configureObjectMapper(result);
        return result;
    }

}
