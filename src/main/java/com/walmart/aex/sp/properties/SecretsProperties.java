package com.walmart.aex.sp.properties;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@Slf4j
public class SecretsProperties {

    private static final String LOCAL = "local";

    @Value("#{systemProperties['sql.username']}")
    private String localSqlUsername;

    @Value("#{systemProperties['sql.password']}")
    private String localSqlPassword;

    @Value("#{systemProperties['sql.cbam.username']}")
    private String localCbamSqlUsername;

    @Value("#{systemProperties['sql.cbam.password']}")
    private String localCbamSqlPassword;

    @Value("#{systemProperties['midasApi.authorization']}")
    private String midasAPIAuthorization;

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    public String fetchSQLServerUserName() throws IOException {
        return activeProfile.contains(LOCAL) ? localSqlUsername : new String(Files.readAllBytes(Paths.get("/etc" +
                "/secrets/sql.username.txt")));
    }

    public String fetchSQLServerPassword() throws IOException {
      return activeProfile.contains(LOCAL) ? localSqlPassword : new String(Files.readAllBytes(Paths.get("/etc" +
                "/secrets/sql.password.txt")));
    }

    public String fetchCBAMSQLServerUserName() throws IOException {
        return activeProfile.contains(LOCAL) ? localCbamSqlUsername : new String(Files.readAllBytes(Paths.get("/etc" +
                "/secrets/cbam.sql.username.txt")));
    }

    public String fetchCBAMSQLServerPassword() throws IOException {
        return activeProfile.contains(LOCAL) ? localCbamSqlPassword : new String(Files.readAllBytes(Paths.get("/etc" +
                "/secrets/cbam.sql.password.txt")));
    }

    public String fetchMidasAPIAuthorization() throws IOException {
        return activeProfile.contains(LOCAL) ? midasAPIAuthorization : new String(Files.readAllBytes(Paths.get("/etc" +
              "/secrets/midasApi.authorization.txt")));
    }

}
