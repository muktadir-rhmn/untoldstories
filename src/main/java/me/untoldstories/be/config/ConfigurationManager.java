package me.untoldstories.be.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.untoldstories.be.config.pojos.DatabaseConfiguration;
import me.untoldstories.be.config.pojos.JWTConfiguration;
import me.untoldstories.be.error.exceptions.InternalServerErrorException;

import java.io.FileInputStream;
import java.io.IOException;

public class ConfigurationManager {
    private final static String configurationFileDirectory = "configs/";

    public static DatabaseConfiguration getDatabaseConfiguration() {
        String fileName = "db_config.json";
        return (DatabaseConfiguration) readConfigurationFromFile(configurationFileDirectory + fileName, DatabaseConfiguration.class);
    }

    public static JWTConfiguration getJWTConfiguration() {
       String fileName = "jwt_config.json";
       return (JWTConfiguration) readConfigurationFromFile(configurationFileDirectory + fileName, JWTConfiguration.class);
    }

    private static Object readConfigurationFromFile(String filePath, Class outputClass) {
       try {
           FileInputStream is = new FileInputStream(filePath);
           ObjectMapper mapper = new ObjectMapper();
           return mapper.readValue(is, outputClass);
       } catch (IOException e) {
           e.printStackTrace();
           throw new InternalServerErrorException(e);
       }
    }
}
