package com.github.gridlts.tbtasksync.google

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource(value = ["classpath:google.properties"])
@ConfigurationProperties(prefix = "gtasks")
class GTaskConfig {
    lateinit var apiKey: String

    lateinit var clientId: String

    lateinit var clientKey: String

    lateinit var scope: String
}
