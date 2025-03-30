package com.github.gridlts.tbtasksync.google

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.tasks.TasksScopes
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.io.File

@Component
@Profile("!test")
class GoogleAuthorization @Autowired constructor(private val gTaskConfig : GTaskConfig,
                           @Qualifier("thunderbirdProfilePath")
                           private val  thunderbirdProfilePath : String) {

    var dataStoreFactory : FileDataStoreFactory? = null
    var httpTransport : NetHttpTransport? = null
    val dataStoreDir : File = File(thunderbirdProfilePath)

    val port : Int = 7100

    // throws Exception
    fun authorize() : Credential {
        val clientSecrets : GoogleClientSecrets = GoogleClientSecrets()
        clientSecrets.setInstalled(GoogleClientSecrets.Details().setClientId(gTaskConfig.clientId)
                .setClientSecret(gTaskConfig.clientKey))
        val flow : GoogleAuthorizationCodeFlow =
            GoogleAuthorizationCodeFlow.Builder(
                httpTransport,
                GTaskRepo.JSON_FACTORY,
                clientSecrets,
                listOf(TasksScopes.TASKS_READONLY)
        ).setDataStoreFactory(
                dataStoreFactory).build()
        // authorize
        return AuthorizationCodeInstalledApp(flow, LocalServerReceiver.Builder().setPort(port).build())
                .authorize("user");
    }

    // throws Exception
    fun main() : Credential {
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        dataStoreFactory = FileDataStoreFactory(dataStoreDir);
        // authorization
        return authorize();
    }
}
