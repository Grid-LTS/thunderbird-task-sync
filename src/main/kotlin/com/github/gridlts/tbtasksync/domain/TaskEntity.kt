package com.github.gridlts.tbtasksync.domain

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.hibernate.Hibernate
import java.io.Serializable
import jakarta.persistence.*

@Entity
@Table(name = "cal_todos")
data class TaskEntity(
    @Id
    @Column(name = "id")
    val id: String,

    @Column(name = "cal_id")
    val calId: String,

    @Column(name = "time_created")
    val timeCreated: Long,

    @Column(name = "last_modified")
    val timeModified: Long,

    @Column(name = "todo_completed")
    var timeCompleted: Long? = null,

    @Column(name = "todo_due")
    val timeDue: Long? = null,

    @Column(name = "flags")
    val flags: Int,

    @Column(name = "title")
    val title: String,

    @Convert(converter = CalStatusEnumConverter::class)
    @Column(name = "ical_status")
    var status: CalStatus,

    @Column(name = "todo_completed_tz")
    val toDoCompletedTz: String? = null,

    @Column(name = "recurrence_id_tz")
    val recurrenceIdTz: Int? = null,

    @Column(name = "recurrence_id")
    val recurrenceId: String? = null,

    @OneToMany(mappedBy = "id.task", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    val descriptions: List<Description>? = null,

    @OneToMany(mappedBy = "id.task", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val properties: List<CalProperties>? = null
) : Serializable {

    constructor() : this(
        id = "",
        calId = "",
        timeCreated = 0L,
        timeModified = 0L,
        timeCompleted = null,
        timeDue = null,
        flags = 0,
        title = "",
        status = CalStatus.NEEDS_ACTION,
        toDoCompletedTz = null,
        recurrenceIdTz = null,
        recurrenceId = null,
        descriptions = null,
        properties = null
    )

    fun isDeleted(): Boolean {
        return status == CalStatus.DELETED
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TaskEntity

        return EqualsBuilder()
            .append(id, other.id)
            .isEquals
    }

    override fun hashCode(): Int = HashCodeBuilder()
        .append(id)
        .toHashCode()
}
