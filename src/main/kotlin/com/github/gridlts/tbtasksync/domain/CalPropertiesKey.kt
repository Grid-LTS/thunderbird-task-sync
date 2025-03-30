package com.github.gridlts.tbtasksync.domain

enum class CalPropertiesKey (val id:String) {
    DESCRIPTION("DESCRIPTION"),
    TRANSP("TRANSP"),
    URL("URL"),
    CLASS("CLASS"),
    SEQUENCE("SEQUENCE"),
    PERCENT_COMPLETE("PERCENT-COMPLETE"),
    X_GOOGLE_SORTKEY("X-GOOGLE-SORTKEY");

    override fun toString(): String {
        return id
    }

    companion object {

        fun getEnum(value: String?): CalPropertiesKey? {
            return CalPropertiesKey.entries.firstOrNull { it.id.equals(value, ignoreCase = true) }
        }

    }

}
