package com.github.gridlts.tbtasksync

import com.github.gridlts.tbtasksync.google.GTaskRepo
import com.github.gridlts.tbtasksync.google.GoogleAuthorization
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@TestConfiguration
class TestConfig {

    @MockBean
    GTaskRepo gTaskRepo

    @Bean
    @Primary
    GoogleAuthorization mockGoogleAuthorization() {
        GoogleAuthorization authorization = mock(GoogleAuthorization.class)
        GoogleCredential mockCredential = new GoogleCredential()
        mockCredential.setAccessToken("gud")
        when(authorization.main()).thenReturn(mockCredential);
        return authorization
    }


}
