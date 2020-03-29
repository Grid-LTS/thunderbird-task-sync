package com.github.gridlts.tbtasksync.domain

import grails.gorm.annotation.Entity
import groovy.transform.ToString

@ToString
@Entity
class TaskEntity {

    String calId
    String id
    long timeCreated
    long timeModified
    Long timeCompleted
    Long timeDue
    String title
    CalStatus status
    static hasMany = [descriptions: Description]
    static mappedBy = [description: "task"]
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
        }
        status enumType: "identity"
    }
}
