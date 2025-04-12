package com.github.gridlts.tbtasksync

import com.github.gridlts.tbtasksync.TestConfig.Companion.ACCESS_TOKEN
import com.github.gridlts.tbtasksync.google.GTaskRepo
import com.github.gridlts.tbtasksync.repo.TaskEntityRepository
import com.google.api.services.tasks.Tasks
import com.google.api.services.tasks.model.TaskList
import com.google.api.services.tasks.model.TaskLists
import jakarta.annotation.PostConstruct
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource


@SpringBootTest
@Import(TestConfig::class)
@ActiveProfiles("integrationtest")
class TbTaskSyncRunnerDeletionIntegrationTest() {

    @Autowired
    lateinit var taskEntityRepository: TaskEntityRepository

    companion object {

        @JvmStatic
        @DynamicPropertySource
        fun overrideProperties(registry: DynamicPropertyRegistry) {
            registry.add("appdata") { "classpath:/." }
            registry.add("user.home") { "classpath:/." }
        }
    }

    @TestConfiguration
    class TestConfig {

        @Autowired
        lateinit var gTaskRepo: GTaskRepo

        @PostConstruct
        fun initMock() {
            val tasksService = mock(Tasks::class.java, RETURNS_DEEP_STUBS)
            val result = mock(TaskLists::class.java)
            val myList = TaskList()
            myList.id="6e0ad98a-87a9-45be-b82e-ac19c53b3bda"
            whenever(result.items).thenReturn(mutableListOf(myList))
            whenever(tasksService.tasklists().list()
                .setMaxResults(anyInt())
                .execute()).thenReturn(result)
            gTaskRepo.tasksService = tasksService
            gTaskRepo.accessToken = ACCESS_TOKEN

            val completedTaskresult = mock(com.google.api.services.tasks.model.Tasks::class.java)
            whenever(result.items).thenReturn(emptyList())
            whenever(tasksService.tasks().list(eq(myList.id))
                .setMaxResults(any())
                .setCompletedMin(any())
                .setShowCompleted(true)
                .setShowHidden(true)
                .execute()).thenReturn(completedTaskresult)
        }
    }

    @Test
    fun whenNoTasksReturn_deleteOpenTasks() {
        assertThat(taskEntityRepository.findAll()).hasSize(0)
    }

}
