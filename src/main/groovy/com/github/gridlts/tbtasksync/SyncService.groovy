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


    GTaskRepo gTaskRepo;

    GoogleAuthorization googleAuth
    String accessToken
    Map<CalStatus, Integer> counter = [completed: 0, deleted: 0]

    SyncService(GTaskRepo gTaskRepo, GoogleAuthorization googleAuth) {
        this.gTaskRepo = gTaskRepo
        this.googleAuth = googleAuth
    }

    @Transactional
    def void sync(){
        Credential creds = googleAuth.main()
        accessToken = creds.accessToken
        gTaskRepo.init(accessToken)
        def lists = gTaskRepo.getTaskLists(accessToken)
        lists.forEach {
            syncTasks(it.getId())
        }
    }

    def void syncTasks(String taskListId) {
        List<Task> deletedTasks = gTaskRepo.getDeletedTasksForTaskList(taskListId)
        deletedTasks.forEach{
            if (it.getDeleted()) {
                def savedTask = TaskEntity.findById(it.getId())
                counter.deleted++
                savedTask.delete()
            }
        }
        List<Task> completedTasks = gTaskRepo.getCompletedTasksForTaskList(taskListId, getOldEnoughDate())
        completedTasks.forEach{
            if (it.getCompleted()) {
                def savedTask = TaskEntity.findById(it.getId())
                if (savedTask.status != CalStatus.COMPLETED) {
                    savedTask.status = CalStatus.COMPLETED
                    savedTask.save()
                    counter.completed++
                }
            }
        }
        println("Found ${counter.deleted} tasks that were not deleted.")
        println("Found ${counter.completed} tasks that were not in state completed.")
    }

    def static ZonedDateTime getOldEnoughDate() {
        String date = "1990-01-01";
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, f);
        return localDate.atStartOfDay(ZoneId.of("UTC"));
    }
}
