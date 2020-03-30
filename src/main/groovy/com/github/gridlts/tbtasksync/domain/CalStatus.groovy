package com.github.gridlts.tbtasksync.domain

enum CalStatus {

    DELETED("DELETED"),
    NEEDS_ACTION("NEEDS-ACTION"),
    COMPLETED("COMPLETED");

    String id

    CalStatus(String status){
        this.id = status
    }
}