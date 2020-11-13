package com.github.gridlts.tbtasksync.domain

import grails.gorm.annotation.Entity
import groovy.transform.ToString
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

@ToString
@Entity
class TaskEntity implements Serializable {

    String calId
    String id
    long timeCreated
    long timeModified
    Long timeCompleted
    Long timeDue
    int flags
    String title
    CalStatus status
    String toDoCompletedTz
    Integer recurrenceIdTz
    String recurrenceId

    static hasMany = [descriptions: Description, properties: CalProperties]
    static mappedBy = [descriptions: "task", properties: "task"]
    static fetchMode = [description: 'eager']
    static mapping = {
        table  "cal_todos"
        version false
        id column: 'id'
        columns {
            calId column: "cal_id"
            timeCreated column: "time_created"
            timeModified column: "last_modified"
            timeCompleted column: "todo_completed"
            timeDue column: "todo_due"
            status column: "ical_status"
            toDoCompletedTz column: "todo_completed_tz"
            recurrenceId column: "recurrence_id"
            recurrenceIdTz column: "recurrence_id_tz"

        }
        status enumType: "identity"
    }

    static constraints = {
        timeDue nullable: true
        toDoCompletedTz nullable: true
        recurrenceId nullable: true
        recurrenceIdTz nullable: true
    }


    boolean equals(other) {
        if (!(other instanceof Description)) {
            return false
        }
        Description that = (Description) other;
        return new EqualsBuilder()
                .append(this.id, that.id)
                .isEquals()
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        builder.append id
        builder.toHashCode()
    }
}
