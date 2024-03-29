package com.github.gridlts.tbtasksync.google

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.tasks.Tasks
import com.google.api.services.tasks.model.Task
import com.google.api.services.tasks.model.TaskList
import com.google.api.services.tasks.model.TaskLists
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

import java.security.GeneralSecurityException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
@Profile("!test") // needs to be mocked
class GTaskRepo {

    static int MAX_RESULTS = 10000

    final static DateTimeFormatter RFC_3339_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZone(ZoneId.of("UTC"))

    static JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance()
    static String APPLICATION_NAME = "Thunderbird Task Sync"

    Tasks tasksService
    String accessToken
    GTaskRepo() {
    }

    void init(String accessToken) throws IOException, GeneralSecurityException {
        if (this.tasksService == null ||
                !this.accessToken.equals(accessToken)) {
            this.accessToken  = accessToken
            GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            this.tasksService = new Tasks.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build()
        }
    }

    List<TaskList> getTaskLists(String accessToken) throws IOException, GeneralSecurityException {
        this.init(accessToken)
        return this.getTaskLists()
    }

    List<TaskList> getTaskLists() throws IOException {
        TaskLists result = this.tasksService.tasklists().list()
                .setMaxResults(10)
                .execute()
        List<TaskList> taskLists = result.getItems()
        if (taskLists == null) {
            taskLists = new ArrayList<>()
        }
        return taskLists
    }

    List<Task> getTasksForTaskListEntry(String taskListId, String accessToken)
            throws IOException, GeneralSecurityException {
        this.init(accessToken)
        return this.getOpenTasksForTaskList(taskListId)
    }

    List<Task> getOpenTasksForTaskList(String taskListId)
            throws IOException {
        com.google.api.services.tasks.model.Tasks result = this.tasksService.tasks().list(taskListId)
                .setMaxResults(MAX_RESULTS)
                .setShowCompleted(false)
                .execute()
        List<Task> tasksForTaskList = result.getItems()
        if (tasksForTaskList == null) {
            tasksForTaskList = new ArrayList<>()
        }
        return tasksForTaskList
    }

    List<Task> getDeletedTasksForTaskList(String taskListId)
            throws IOException {
        com.google.api.services.tasks.model.Tasks result = this.tasksService.tasks().list(taskListId)
                .setMaxResults(MAX_RESULTS)
                .setShowCompleted(false)
                .setShowDeleted(true)
                .execute()
        List<Task> tasksForTaskList = result.getItems()
        if (tasksForTaskList == null) {
            tasksForTaskList = new ArrayList<>()
        }
        return tasksForTaskList
    }

    Task queryTask(String taskListId, String taskId) {
        return this.tasksService.tasks().get(taskListId, taskId).execute()
    }

    List<Task> getCompletedTasksForTaskList(String taskListId, ZonedDateTime newerThanDateTime)
            throws IOException {
        com.google.api.services.tasks.model.Tasks result = this.tasksService.tasks().list(taskListId)
                .setMaxResults(MAX_RESULTS)
                .setCompletedMin(convertZoneDateTimeToRFC3339Timestamp(newerThanDateTime))
                .setShowCompleted(true)
                .setShowHidden(true)
                .execute()
        List<Task> tasksForTaskList = result.getItems()
        if (tasksForTaskList == null) {
            tasksForTaskList = new ArrayList<>()
        }
        return tasksForTaskList;
    }

    static String convertZoneDateTimeToRFC3339Timestamp(ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(RFC_3339_FORMATTER)
    }
}
