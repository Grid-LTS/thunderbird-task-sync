package com.github.gridlts.tbtasksync

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource


@SpringBootTest
@Import(TestConfig::class)
@ActiveProfiles("integrationtest")
class TbTaskSyncApplicationTests() {

    companion object {

        @JvmStatic
        @DynamicPropertySource
        fun overrideProperties(registry: DynamicPropertyRegistry) {
            registry.add("appdata") { "classpath:/." }
            registry.add("user.home") { "classpath:/." }
        }
    }

    @Test
    fun contextLoads() {
        // Your test code here
    }

}
