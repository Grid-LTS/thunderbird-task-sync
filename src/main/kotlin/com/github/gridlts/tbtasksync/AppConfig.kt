package com.github.gridlts.tbtasksync

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment
import java.nio.file.Path
import javax.sql.DataSource
import java.nio.file.Paths

@Configuration
class AppConfig {
    @Value("\${thunderbird.profile}")
    private lateinit var thunderbirdProfile: String

    @Autowired
    private lateinit var env: Environment

    @Bean
    fun thunderbirdProfilePath(): Path {
        return if (env.activeProfiles.contains("test")) {
            Paths.get(System.getProperty("user.dir"),"tmp")
        } else {
            var osName = System.getProperty("os.name").lowercase()
            val thunderbirdProfileParentDir = if (osName.contains("mac")) {
                Paths.get(System.getProperty("user.home"),"Library","Thunderbird", "Profiles")
            } else if (osName.startsWith("windows")) {
                Paths.get(System.getenv("appdata"),"Thunderbird", "Profiles")
            } else {
                Paths.get(System.getProperty("user.home"), ".thunderbird")
            }
            thunderbirdProfileParentDir.resolve(thunderbirdProfile)
        }
    }

    @Bean
    @Profile("!test")
    fun getDataSource(@Qualifier("thunderbirdProfilePath") thunderbirdProfilePath: Path): DataSource {
        val url = "jdbc:sqlite:file:${thunderbirdProfilePath.toUri().path}/calendar-data/cache.sqlite"
        return DataSourceBuilder.create().url(url).build()
    }
}

