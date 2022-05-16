package com.walmart.aex.sp.properties;


import io.strati.ccm.utils.client.annotation.Configuration;
import io.strati.ccm.utils.client.annotation.Property;

@Configuration(configName = "databaseConfig")
public interface DatabaseProperties {
    @Property(propertyName = "sqlServer.url")
    String getUrl();

    @Property(propertyName = "sqlServer.cbam.url")
    String getCbamUrl();

    @Property(propertyName = "sqlServer.driverClassName")
    String getDriver();

    @Property(propertyName = "sqlServer.connection.pool.initial.size")
    int getConnectionPoolInitialSize();

    @Property(propertyName = "sqlServer.connection.max.active")
    int getMaxActiveConnection();

    @Property(propertyName = "sqlServer.connection.timeout.ms")
    int getConnectionTimeOut();

    @Property(propertyName = "sqlServer.connection.max.life.time.ms")
    int getMaxLifeTime();

    @Property(propertyName = "sqlServer.connection.idle.timeout.ms")
    int getIdleTimeOut();
}
