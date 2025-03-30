package com.github.gridlts.tbtasksync.domain

class CalStatusEnumConverter : CustomEnumConverter<CalStatus>() {

    override fun convertToEntityAttribute(dbData: String?): CalStatus? {
        if (dbData == null) return null
        return CalStatus.getEnum(dbData)
    }
}
