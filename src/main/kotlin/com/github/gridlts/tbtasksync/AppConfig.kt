package com.github.gridlts.tbtasksync

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment
import javax.sql.DataSource

@Configuration
class AppConfig {

    @Value("\${thunderbird.profile}")
    private lateinit var thunderbirdProfile: String

    @Autowired
    private lateinit var env: Environment

    @Bean
    fun thunderbirdProfilePath(): String {
        return if (env.activeProfiles.contains("test")) {
            "${System.getProperty("user.dir")}/tmp"
        } else {
            val thunderbirdProfileParentDir = if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                "Library/Thunderbird/Profiles"
            } else {
                ".thunderbird"
            }
            "${System.getProperty("user.home")}/$thunderbirdProfileParentDir/$thunderbirdProfile"
        }
    }

    @Bean
    @Profile("!test")
    fun getDataSource(@Qualifier("thunderbirdProfilePath") thunderbirdProfilePath: String): DataSource {
        val url = "jdbc:sqlite:file:$thunderbirdProfilePath/calendar-data/cache.sqlite"
        return DataSourceBuilder.create().url(url).build()
    }
}

