package com.github.gridlts.tbtasksync

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
class AppConfig {

    @Value(value = '${thunderbird.profile}')
    private String thunderbirdProfile

    @Bean
    String thunderbirdProfilePath() {
        String thunderbirdProfileParentDir;
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            thunderbirdProfileParentDir = "Library/Thunderbird/Profiles"
        } else {
            thunderbirdProfileParentDir = ".thunderbird"
        }
        return "${System.getProperty("user.home")}/${thunderbirdProfileParentDir}/${thunderbirdProfile}"
    }

    @Bean
    DataSource getDataSource(@Qualifier("thunderbirdProfilePath") String thunderbirdProfilePath) {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create()
        String url = "jdbc:sqlite:file:${thunderbirdProfilePath}/calendar-data/cache.sqlite"
        dataSourceBuilder.url(url)
        return dataSourceBuilder.build()
    }
}

