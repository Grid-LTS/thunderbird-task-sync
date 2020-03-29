package com.github.gridlts.tbtasksync.domain

enum CalStatus {

    NEEDS_ACTION("NEEDS-ACTION"),
    COMPLETED("COMPLETED");

    String id

    CalStatus(String status){
        this.id = status
    }
}