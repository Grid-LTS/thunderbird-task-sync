package com.github.gridlts.tbtasksync.domain

enum CalPropertiesKey {

    DESCRIPTION("DESCRIPTION"),
    TRANSP("TRANSP"),
    URL("URL"),
    CLASS("CLASS"),
    SEQUENCE("SEQUENCE"),
    X_GOOGLE_SORTKEY("X-GOOGLE-SORTKEY");

    String id

    CalPropertiesKey(String key){
        this.id = key
    }
}