package com.github.gridlts.tbtasksync

import com.github.gridlts.tbtasksync.domain.CalStatus
import com.github.gridlts.tbtasksync.domain.TaskEntity
import com.github.gridlts.tbtasksync.google.GTaskRepo
import com.github.gridlts.tbtasksync.google.GoogleAuthorization
import com.google.api.client.auth.oauth2.Credential
import com.google.api.services.tasks.model.Task
import grails.gorm.transactions.Transactional
import org.springframework.stereotype.Component

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Component
class SyncService {

    GTaskRepo gTaskRepo

    GoogleAuthorization googleAuth
    String accessToken
    Map<CalStatus, Integer> counter = [completed: 0, deleted: 0, duplicate: 0]
    Map<String, TaskEntity> duplicatesMap = [:]
    Map<String, TaskEntity> savedTasksMap = [:]
    Map<String, String> tasksList = [:]

    SyncService(GTaskRepo gTaskRepo, GoogleAuthorization googleAuth) {
        this.gTaskRepo = gTaskRepo
        this.googleAuth = googleAuth
    }

    @Transactional
    void sync() {
        TaskEntity.findAll().each {
            savedTasksMap[it.id] = it
        }
        Credential creds = googleAuth.main()
        accessToken = creds.accessToken
        gTaskRepo.init(accessToken)
        def lists = gTaskRepo.getTaskLists(accessToken)
        lists.forEach {
            syncTasks(it.getId())
        }
        duplicatesMap.each { key, value ->
            println(value)
            TaskEntity.executeUpdate("delete TaskEntity t where t.id = :id and t.calId = :calId and t.timeCreated = :timeCreated",
                    [id: value.id, calId: value.calId, timeCreated: value.timeCreated])
            counter.duplicate++
        }
        println("Found ${counter.deleted} tasks that were not deleted.")
        println("Found ${counter.completed} tasks that were not in state completed.")
        println("Found ${counter.duplicate} tasks that were duplicated.")

        List<TaskEntity> obsoleteTasks = savedTasksMap.values().findAll {
            this.tasksList.keySet().contains(it.calId)
        }
        println("Found ${obsoleteTasks.size()} that are not available upstream and are deleted.")
        obsoleteTasks.forEach {
            println(it.title)
            it.delete(flush: true)
        }
    }

    void removeDuplicates(Task task) {
        def duplicates = TaskEntity.executeQuery(
                "select new map(t.calId as calId, t.id as id, t.timeCreated as timeCreated) from TaskEntity t where t.title = :title",
                [title: task.getTitle()])
        if (duplicates.size() == 1) {
            return
        }
        // keep task is the task that is not discarded
        TaskEntity keepTask = duplicates.find { it.id == task.getId() }
        if (!keepTask) {
            return
        }
        // ensure that the keep task is not marked as duplicate (and therefore eventually deleted)
        if (duplicatesMap.containsKey(keepTask.id)) {
            duplicatesMap.remove(keepTask.id)
            return
        }
        // obvious duplicates (not identicals)
        def obvious = duplicates.findAll { it.id != keepTask.id }
        obvious.forEach {
            duplicatesMap[it.id] = it
        }
        // find newest if multiple tasks with the same id exist
        def identicals = duplicates.findAll { it.id == keepTask.id }
        long maxTime = identicals.collect { it.timeCreated }.max()
        identicals.findAll { it.timeCreated != maxTime }.forEach {
            duplicatesMap[it.id] = it
        }
    }

    @Transactional
    void syncTasks(String taskListId) {
        List<Task> openTasks = gTaskRepo.getOpenTasksForTaskList(taskListId)
        if (openTasks.size() > 0) {
            def firstTask = TaskEntity.findById(openTasks[0].getId())
            addTaskListMapping(firstTask.calId, taskListId)
        }
        openTasks.forEach {
            removeDuplicates(it)
            savedTasksMap.remove(it.getId())
        }
        List<Task> deletedTasks = gTaskRepo.getDeletedTasksForTaskList(taskListId)
        deletedTasks.forEach {
            if (it.getDeleted()) {
                savedTasksMap.remove(it.getId())
                def savedTask = TaskEntity.findById(it.getId())
                if (savedTask != null) {
                    counter.deleted++
                    println("Task ${savedTask.title} was marked as deleted, but was not deleted")
                    savedTask.delete(flush: true)
                }
            }
        }
        List<Task> completedTasks = gTaskRepo.getCompletedTasksForTaskList(taskListId, getOldEnoughDate())
        if (completedTasks.size() > 0) {
            def firstTask = TaskEntity.findById(completedTasks[0].getId())
            addTaskListMapping(firstTask.calId, taskListId)
        }
        completedTasks.forEach {
            if (it.getStatus() == "completed") {
                savedTasksMap.remove(it.getId())
                def savedTask = TaskEntity.findById(it.getId())
                if (!savedTask) {
                    return
                }
                if (savedTask.status != CalStatus.COMPLETED) {
                    savedTask.status = CalStatus.COMPLETED
                    savedTask.timeCompleted = parseRFC_3339Date(it.getCompleted().toStringRfc3339())
                    savedTask.save(flush: true)
                    counter.completed++
                }
                removeDuplicates(it)
            }
        }
    }

    void addTaskListMapping(String calId, String gTaskListId) {
        if (!this.tasksList.containsKey(calId)) {
            this.tasksList[calId] = gTaskListId
        }
    }

    static Long parseRFC_3339Date(String dateTimeRfc3339) {
        String RFC_3339_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        DateTimeFormatter RFC_3339_FORMATTER = DateTimeFormatter
                .ofPattern(RFC_3339_PATTERN)
                .withZone(ZoneId.of("UTC"))
        def theDateTime = ZonedDateTime.parse(dateTimeRfc3339, RFC_3339_FORMATTER)
        return theDateTime.toInstant().toEpochMilli() * 1000
    }

    static ZonedDateTime getOldEnoughDate() {
        String date = "1990-01-01";
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, f);
        return localDate.atStartOfDay(ZoneId.of("UTC"));
    }
}
