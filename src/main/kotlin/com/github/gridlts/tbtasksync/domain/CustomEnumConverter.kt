package com.github.gridlts.tbtasksync.domain

import jakarta.persistence.AttributeConverter

open class CustomEnumConverter<T> : AttributeConverter<T, String> {

    override fun convertToDatabaseColumn(attribute: T?): String? {
        return attribute?.toString()
    }

    override fun convertToEntityAttribute(dbData: String?): T? {
        throw IllegalArgumentException("Unknown enum type for value $dbData")

    }

}
