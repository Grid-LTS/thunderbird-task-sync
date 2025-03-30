package com.github.gridlts.tbtasksync.repo

import com.github.gridlts.tbtasksync.domain.TaskEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TaskEntityRepository : JpaRepository<TaskEntity, String> {

    @Query("SELECT t FROM TaskEntity t WHERE t.title = :title")
    fun findByTitle(@Param("title") title: String): List<TaskEntity>

    @Query("SELECT t FROM TaskEntity t WHERE t.id = :id")
    override fun findById(id: String): Optional<TaskEntity>


    @Query("DELETE FROM TaskEntity t WHERE t.id = :id AND t.calId = :calId AND t.timeCreated = :timeCreated")
    fun deleteByIdCalIdAndTimeCreated(@Param("id") id: String, @Param("calId") calId: String, @Param("timeCreated") timeCreated: Long): Int

    @Query("SELECT t FROM TaskEntity t")
    fun findAllTasks(): List<TaskEntity>
}
