package com.github.gridlts.tbtasksync.domain

enum class CalStatus (val id : String) {

    DELETED("DELETED"),
    NEEDS_ACTION("NEEDS-ACTION"),
    DUPLICATE("DUPLICATE"),
    COMPLETED("COMPLETED");

    override fun toString(): String {
        return id
    }

    companion object {

        fun getEnum(value: String?): CalStatus? {
            return CalStatus.entries.firstOrNull() { it.id.equals(value, ignoreCase = true) }
        }
    }


}
