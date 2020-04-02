package com.github.gridlts.tbtasksync;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DbConfig {

    @Value("${thunderbird.profile}")
    private String thunderbirdProfile;

    @Bean
    public DataSource getDataSource() {
        String thunderbirdProfileParentDir;
        if (System.getProperty("os.name").toLowerCase().contains("mac")){
            thunderbirdProfileParentDir = "Library/Thunderbird/Profiles";
        } else {
            thunderbirdProfileParentDir = ".thunderbird";
        }
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        String url =  String.format("jdbc:sqlite:file:%s/%s/%s/calendar-data/cache.sqlite",
                System.getProperty("user.home"), thunderbirdProfileParentDir, thunderbirdProfile );
        dataSourceBuilder.url(url
               );
        return dataSourceBuilder.build();
    }
}
