package com.github.gridlts.tbtasksync.domain

import grails.gorm.annotation.Entity
import groovy.transform.ToString

@ToString
@Entity
class TaskEntity {

    String calId
    String id
    long timeCreated
    String title
    String status

    static mapping = {
        table  "cal_todos"
        version false
        columns {
            calId column: "cal_id"
            timeCreated column: "time_created"
            status column: "ical_status"
        }
    }
}
