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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

import java.security.GeneralSecurityException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.jvm.Throws

@Service
@Profile("!test") // needs to be mocked
class GTaskRepo () {
    companion object {
        const val MAX_RESULTS: Int = 10000
        val RFC_3339_FORMATTER : DateTimeFormatter = DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .withZone(ZoneId.of("UTC"))
        fun convertZoneDateTimeToRFC3339Timestamp(zonedDateTime : ZonedDateTime ) : String
        {
            return zonedDateTime.format(RFC_3339_FORMATTER)
        }
        val JSON_FACTORY : JsonFactory = JacksonFactory.getDefaultInstance()
        val APPLICATION_NAME : String = "Thunderbird Task Sync"
    }

    var tasksService : Tasks? = null
    var accessToken : String? = null

    // throws IOException, GeneralSecurityException
    fun init(accessToken : String)  {
        if (tasksService == null || this.accessToken != accessToken) {
            this.accessToken  = accessToken
            val credential : GoogleCredential = GoogleCredential().setAccessToken(accessToken);
            val HTTP_TRANSPORT : NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
            this.tasksService = Tasks.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build()
        }
    }

    // throws IOException, GeneralSecurityException
    fun getTaskLists(accessToken : String) : List<TaskList>   {
        this.init(accessToken)
        return this.getTaskLists()
    }

    //  throws IOException
    fun getTaskLists() : List<TaskList>  {
        val result : TaskLists = this.tasksService!!.tasklists().list()
                .setMaxResults(10)
                .execute()
        var taskLists : List<TaskList> = result.getItems()
        if (taskLists == null) {
            taskLists = mutableListOf()
        }
        return taskLists
    }

    //  throws IOException, GeneralSecurityException
    fun getTasksForTaskListEntry(taskListId : String, accessToken : String ) : List<Task>
    {
        this.init(accessToken)
        return this.getOpenTasksForTaskList(taskListId)
    }

    // throws IOException
    fun getOpenTasksForTaskList(taskListId : String) : List<Task>
     {
        val result : com.google.api.services.tasks.model.Tasks = this.tasksService!!.tasks().list(taskListId)
                .setMaxResults(MAX_RESULTS)
                .setShowCompleted(false)
                .execute()
        var tasksForTaskList : List<Task>  = result.getItems()
        if (tasksForTaskList == null) {
            tasksForTaskList = listOf()
        }
        return tasksForTaskList
    }

    // throws IOException
    fun getDeletedTasksForTaskList(taskListId : String) : List<Task> {
        val result : com.google.api.services.tasks.model.Tasks = this.tasksService!!.tasks().list(taskListId)
                .setMaxResults(MAX_RESULTS)
                .setShowCompleted(false)
                .setShowDeleted(true)
                .execute()
        var tasksForTaskList : List<Task> = result.getItems()
        if (tasksForTaskList == null) {
            tasksForTaskList = listOf()
        }
        return tasksForTaskList
    }

    fun queryTask( taskListId : String, taskId : String) : Task{
        return this.tasksService!!.tasks().get(taskListId, taskId).execute()
    }

    // throws IOException

    fun getCompletedTasksForTaskList(taskListId : String , newerThanDateTime: ZonedDateTime ) :
    List<Task> {
        val result : com.google.api.services.tasks.model.Tasks =
            this.tasksService!!.tasks().list(taskListId)
                .setMaxResults(MAX_RESULTS)
                .setCompletedMin(convertZoneDateTimeToRFC3339Timestamp(newerThanDateTime))
                .setShowCompleted(true)
                .setShowHidden(true)
                .execute()
       var tasksForTaskList :  List<Task>  = result.getItems()
        if (tasksForTaskList == null) {
            tasksForTaskList = listOf()
        }
        return tasksForTaskList;
    }
}
