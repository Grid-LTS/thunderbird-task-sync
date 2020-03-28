package com.github.gridlts.tbtasksync.google

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.tasks.TasksScopes

class GoogleAuthorization {

    private dataStoreFactory;

    private httpTransport;

    private static final File DATA_STORE_DIR =
            new File(System.getProperty("user.home"), "/var/kanban-hub");

    private Integer port = 7100


    def Credential authorize() throws Exception {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(GTaskRepo.JSON_FACTORY,
                new InputStreamReader(GoogleAuthorization.class.getResourceAsStream("/client_secrets.json")));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, GTaskRepo.JSON_FACTORY, clientSecrets,
                Collections.singleton(TasksScopes.TASKS_READONLY)).setDataStoreFactory(
                dataStoreFactory).build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver.Builder().setPort(7100).build()).authorize("user");
    }

    def Credential main() throws Exception {
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
        // authorization
        return authorize();
    }
}
