package com.github.gridlts.tbtasksync.domain

import jakarta.persistence.*
import org.hibernate.Hibernate
import java.io.Serializable

@Entity
@Table(name = "cal_properties")
data class CalProperties(
    @EmbeddedId
    val id: CalPropertiesId,

    @Column(name = "cal_id")
    val calId: String,

    @Column(name = "recurrence_id")
    val recurrenceId: String? = null,

    @Column(name = "recurrence_id_tz")
    val recurrenceIdTz: String? = null,

    @Column(name = "value")
    val propertiesValue: String,

) : Serializable {

    constructor() : this(
        id = CalPropertiesId(),
        calId = "",
        recurrenceId = null,
        recurrenceIdTz = null,
        propertiesValue = "",
    )
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as CalProperties

        return calId == other.calId &&
                id.task == other.id.task &&
                id.propertiesKey == other.id.propertiesKey
    }

    override fun hashCode(): Int = java.util.Objects.hash(calId)

    override fun toString(): String {
        return "CalProperties(id=$id, calId='$calId', recurrenceId=$recurrenceId, recurrenceIdTz=$recurrenceIdTz, propertiesValue='$propertiesValue')"
    }

}

@Embeddable
data class CalPropertiesId(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    val task: TaskEntity,

    @Convert(converter = CalPropertiesKeyConverter::class)
    @Column(name = "key")
    val propertiesKey: CalPropertiesKey
) : Serializable {
    constructor() : this(task = TaskEntity(), propertiesKey = CalPropertiesKey.DESCRIPTION)

    override fun toString(): String {
        return "CalPropertiesId(task=${task.id}, propertiesKey=$propertiesKey)"
    }

}
