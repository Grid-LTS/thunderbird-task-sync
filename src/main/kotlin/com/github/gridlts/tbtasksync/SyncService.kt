package com.github.gridlts.tbtasksync

import com.github.gridlts.tbtasksync.domain.CalStatus
import com.github.gridlts.tbtasksync.domain.TaskEntity
import com.github.gridlts.tbtasksync.google.GTaskRepo
import com.github.gridlts.tbtasksync.google.GoogleAuthorization
import com.github.gridlts.tbtasksync.repo.TaskEntityRepository
import com.google.api.client.auth.oauth2.Credential
import com.google.api.services.tasks.model.Task
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Component
class SyncService(
    private val gTaskRepo: GTaskRepo,
    private val googleAuth: GoogleAuthorization,
    private val taskEntityRepository: TaskEntityRepository
) {

    private lateinit var accessToken: String
    private val counter = mutableMapOf(CalStatus.COMPLETED to 0, CalStatus.DELETED to 0, CalStatus.DUPLICATE to 0)
    private val duplicatesMap = mutableMapOf<String, TaskEntity>()
    private val savedTasksMap = mutableMapOf<String, TaskEntity>()
    private val tasksList = mutableMapOf<String, String>()

    @Transactional
    fun sync() {
        taskEntityRepository.findAll().forEach {
            savedTasksMap[it.id] = it
        }
        val creds: Credential = googleAuth.main()
        accessToken = creds.accessToken
        gTaskRepo.init(accessToken)
        val lists = gTaskRepo.getTaskLists(accessToken)
        lists.forEach {
            syncTasks(it.id)
        }
        duplicatesMap.forEach { (_, value) ->
            println(value)
            deleteTask(value)
            counter[CalStatus.DUPLICATE] = counter[CalStatus.DUPLICATE]!! + 1
        }
        println("Found ${counter[CalStatus.DELETED]} tasks that were not deleted.")
        println("Found ${counter[CalStatus.COMPLETED]} tasks that were not in state completed.")
        println("Found ${counter[CalStatus.DUPLICATE]} tasks that were duplicated.")

        val obsoleteTasks = savedTasksMap.values.filter {
            !tasksList.keys.contains(it.calId)
        }
        println("Found ${obsoleteTasks.size} that are not available upstream and are deleted.")
        obsoleteTasks.forEach {
            println(it.title)
            deleteTask(it)
        }
    }


    fun syncTasks(taskListId: String) {
        val openTasks = gTaskRepo.getOpenTasksForTaskList(taskListId)
        if (openTasks.isNotEmpty()) {
            var start = 0
            while (start < openTasks.size) {
                val firstTask = taskEntityRepository.findById(openTasks[start].id).orElse(null)
                if (firstTask == null) {
                    start++
                    if (start == openTasks.size) {
                        return
                    }
                    continue
                }
                addTaskListMapping(firstTask.calId, taskListId)
                break
            }
        }
        openTasks.forEach {
            removeDuplicates(it)
            savedTasksMap.remove(it.id)
        }
        val deletedTasks = gTaskRepo.getDeletedTasksForTaskList(taskListId)
        deletedTasks.forEach {
            if (it.deleted == true) {
                savedTasksMap.remove(it.id)
                val savedTask = taskEntityRepository.findById(it.id).orElse(null)
                if (savedTask != null) {
                    counter[CalStatus.DELETED] = counter[CalStatus.DELETED]!! + 1
                    println("Task ${savedTask.title} was marked as deleted, but was not deleted")
                    taskEntityRepository.delete(savedTask)
                }
            }
        }
        val completedTasks = gTaskRepo.getCompletedTasksForTaskList(taskListId, getOldEnoughDate())
        if (completedTasks.isNotEmpty()) {
            val firstTask = taskEntityRepository.findById(completedTasks[0].id).orElse(null)
            if (firstTask != null) {
                addTaskListMapping(firstTask.calId, taskListId)
            }
        }
        completedTasks.forEach {
            if (it.status == "completed") {
                savedTasksMap.remove(it.id)
                val savedTask = taskEntityRepository.findById(it.id).orElse(null) ?: return@forEach
                if (savedTask.status != CalStatus.COMPLETED) {
                    savedTask.status = CalStatus.COMPLETED
                    savedTask.timeCompleted = parseRFC_3339Date(it.completed)
                    taskEntityRepository.save(savedTask)
                    counter[CalStatus.COMPLETED] = counter[CalStatus.COMPLETED]!! + 1
                }
                removeDuplicates(it)
            }
        }
    }

    fun removeDuplicates(task: Task) {
        val duplicates = taskEntityRepository.findByTitle(task.title)
        if (duplicates.size == 1) {
            return
        }
        val keepTask = duplicates.find { it.id == task.id } ?: return
        if (duplicatesMap.containsKey(keepTask.id)) {
            duplicatesMap.remove(keepTask.id)
            return
        }
        val obvious = duplicates.filter { it.id != keepTask.id }
        obvious.forEach {
            duplicatesMap[it.id] = it
        }
        val identicals = duplicates.filter { it.id == keepTask.id }
        val maxTime = identicals.map { it.timeCreated }.maxOrNull() ?: return
        identicals.filter { it.timeCreated != maxTime }.forEach {
            duplicatesMap[it.id] = it
        }
    }

    fun deleteTask(task: TaskEntity) {
        taskEntityRepository.delete(task)
    }

    fun addTaskListMapping(calId: String, gTaskListId: String) {
        if (!tasksList.containsKey(calId)) {
            tasksList[calId] = gTaskListId
        }
    }

    companion object {
        fun parseRFC_3339Date(dateTimeRfc3339: String): Long {
            val RFC_3339_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            val RFC_3339_FORMATTER = DateTimeFormatter
                .ofPattern(RFC_3339_PATTERN)
                .withZone(ZoneId.of("UTC"))
            val theDateTime = ZonedDateTime.parse(dateTimeRfc3339, RFC_3339_FORMATTER)
            return theDateTime.toInstant().toEpochMilli()
        }

        fun getOldEnoughDate(): ZonedDateTime {
            val date = "1990-01-01"
            val f = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val localDate = LocalDate.parse(date, f)
            return localDate.atStartOfDay(ZoneId.of("UTC"))
        }
    }

}
