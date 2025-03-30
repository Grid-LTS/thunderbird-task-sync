package com.github.gridlts.tbtasksync

import com.github.gridlts.tbtasksync.google.GTaskRepo
import com.github.gridlts.tbtasksync.google.GoogleAuthorization
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.mockito.Mockito.*

@TestConfiguration
class TestConfig {

    @MockBean
    lateinit var gTaskRepo: GTaskRepo

    @Bean
    @Primary
    fun mockGoogleAuthorization(): GoogleAuthorization {
        val authorization = mock(GoogleAuthorization::class.java)
        val mockCredential = GoogleCredential()
        mockCredential.accessToken = "gud"
        `when`(authorization.main()).thenReturn(mockCredential)
        return authorization
    }
}
