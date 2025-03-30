package com.github.gridlts.tbtasksync.domain

class CalPropertiesKeyConverter : CustomEnumConverter<CalPropertiesKey>() {

    override fun convertToEntityAttribute(dbData: String?): CalPropertiesKey? {
        if (dbData == null) return null
        return CalPropertiesKey.getEnum(dbData)
    }
}
