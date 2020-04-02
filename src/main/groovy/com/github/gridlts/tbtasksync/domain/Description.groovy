package com.github.gridlts.tbtasksync.domain

import grails.gorm.annotation.Entity
import groovy.transform.ToString
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

@ToString
@Entity
class Description implements Serializable {

    CalPropertiesKey propertiesKey
    String value
    String calId
    String recurrenceId
    String recurrenceIdTz

    static belongsTo = [task: TaskEntity]

    static mapping = {
        table "cal_properties"
        version false
        columns {
            propertiesKey column: "key"
            value column: "value"
            calId column: "cal_id"
            recurrenceId column: "recurrence_id"
            recurrenceIdTz column: 'recurrence_id_tz'
            task column: "item_id"
        }
        id composite: ['task', 'calId', 'propertiesKey']
        propertiesKey enumType: "identity"
    }

    static constraints = {
        recurrenceId nullable: true
        recurrenceIdTz nullable: true
    }

    boolean equals(other) {
        if (!(other instanceof Description)) {
            return false
        }
        Description that = (Description) other;
        return new EqualsBuilder()
                .append(this.calId, that.calId)
                .append(this.task, that.task)
                .append(this.propertiesKey, that.propertiesKey)
                .isEquals()
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        builder.append calId
        builder.append task
        builder.append propertiesKey
        builder.toHashCode()
    }
}
