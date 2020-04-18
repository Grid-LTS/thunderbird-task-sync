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

@Component
@Profile("!test")
class GoogleAuthorization {

    FileDataStoreFactory dataStoreFactory

    NetHttpTransport httpTransport

    GTaskConfig gTaskConfig

    String thunderbirdProfilePath

    final File dataStoreDir =
            new File(thunderbirdProfilePath)

    Integer port = 7100

    @Autowired
    GoogleAuthorization(GTaskConfig gTaskConfig, @Qualifier("thunderbirdProfilePath")
            String thunderbirdProfilePath) {
        this.gTaskConfig = gTaskConfig
        this.thunderbirdProfilePath = thunderbirdProfilePath
    }

    Credential authorize() throws Exception {
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets()
        clientSecrets.setInstalled(new GoogleClientSecrets.Details().setClientId(gTaskConfig.clientId)
                .setClientSecret(gTaskConfig.clientKey))
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, GTaskRepo.JSON_FACTORY, clientSecrets,
                Collections.singleton(TasksScopes.TASKS_READONLY)).setDataStoreFactory(
                dataStoreFactory).build()
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver.Builder().setPort(port).build())
                .authorize("user");
    }

    Credential main() throws Exception {
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        dataStoreFactory = new FileDataStoreFactory(dataStoreDir);
        // authorization
        return authorize();
    }
}
