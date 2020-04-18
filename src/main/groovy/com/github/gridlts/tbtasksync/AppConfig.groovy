package com.github.gridlts.tbtasksync

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
class AppConfig {

    @Value(value = '${thunderbird.profile}')
    private String thunderbirdProfile

    @Autowired
    Environment env;

    @Bean
    String thunderbirdProfilePath() {
        if (env.activeProfiles.contains("test")) {
            return "${System.getProperty("user.dir")}/tmp"
        }
        String thunderbirdProfileParentDir;
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            thunderbirdProfileParentDir = "Library/Thunderbird/Profiles"
        } else {
            thunderbirdProfileParentDir = ".thunderbird"
        }
        return "${System.getProperty("user.home")}/${thunderbirdProfileParentDir}/${thunderbirdProfile}"
    }

    @Bean
    @Profile("!test")
    DataSource getDataSource(@Qualifier("thunderbirdProfilePath") String thunderbirdProfilePath) {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create()
        String url = "jdbc:sqlite:file:${thunderbirdProfilePath}/calendar-data/cache.sqlite"
        dataSourceBuilder.url(url)
        return dataSourceBuilder.build()
    }
}

