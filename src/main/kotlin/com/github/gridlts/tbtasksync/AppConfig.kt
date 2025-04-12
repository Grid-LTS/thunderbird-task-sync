package com.github.gridlts.tbtasksync

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.ResourceLoader
import java.nio.file.Path
import java.nio.file.Paths
import javax.sql.DataSource


@Configuration
class AppConfig {
    @Value("\${thunderbird.profile}")
    private lateinit var thunderbirdProfile: String


    @Autowired
    private lateinit var resourceLoader: ResourceLoader


    @Value("\${user.home}")
    private lateinit var userHomeDir: String

    @Autowired
    private lateinit var env: Environment


    @Bean
    fun thunderbirdProfilePath(): Path {
        return if (env.activeProfiles.contains("test") || env.activeProfiles.contains("integrationtest")) {
            return Paths.get(resourceLoader.getResource("classpath:$thunderbirdProfile").file.path)
        } else {
            var osName = System.getProperty("os.name").lowercase()
            val thunderbirdProfileParentDir = if (osName.contains("mac")) {
                Paths.get(userHomeDir,"Library","Thunderbird", "Profiles")
            } else if (osName.startsWith("windows")) {
                Paths.get(System.getenv("appdata"),"Thunderbird", "Profiles")
            } else {
                Paths.get(userHomeDir, ".thunderbird")
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

