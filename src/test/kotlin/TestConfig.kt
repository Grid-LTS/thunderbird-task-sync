package com.github.gridlts.tbtasksync

import com.github.gridlts.tbtasksync.google.GoogleAuthorization
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class TestConfig {

    companion object {
        const val ACCESS_TOKEN = "gud"
    }

    @Bean
    @Primary
    fun mockGoogleAuthorization(): GoogleAuthorization {
        val authorization = mock(GoogleAuthorization::class.java)
        val mockCredential = GoogleCredential()
        mockCredential.accessToken = ACCESS_TOKEN
        `when`(authorization.main()).thenReturn(mockCredential)
        return authorization
    }
}
