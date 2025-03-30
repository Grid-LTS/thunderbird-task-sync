package com.github.gridlts.tbtasksync.domain

import org.hibernate.Hibernate
import java.io.Serializable
import jakarta.persistence.*

@Entity
@Table(name = "cal_properties")
data class Description(
    @EmbeddedId
    val id: DescriptionId,

    @Column(name = "cal_id")
    val calId: String,

    @Column(name = "recurrence_id")
    val recurrenceId: String? = null,

    @Column(name = "recurrence_id_tz")
    val recurrenceIdTz: String? = null,

    @Column(name = "value")
    val value: String,

) : Serializable {

    constructor() : this(
        id = DescriptionId(),
        calId = "",
        recurrenceId = null,
        recurrenceIdTz = null,
        value = "",
    )


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Description

        return calId == other.calId &&
                id.task == other.id.task &&
                id.propertiesKey == other.id.propertiesKey
    }

    override fun hashCode(): Int = java.util.Objects.hash(calId)
}

@Embeddable
data class DescriptionId(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    val task: TaskEntity,

    @Convert(converter = CalPropertiesKeyConverter::class)
    @Column(name = "key")
    val propertiesKey: CalPropertiesKey
) : Serializable {
    // No-arg constructor
    constructor() : this(task = TaskEntity(), propertiesKey = CalPropertiesKey.DESCRIPTION)
}
