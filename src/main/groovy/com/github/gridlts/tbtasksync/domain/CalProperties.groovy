package com.github.gridlts.tbtasksync.domain

import grails.gorm.annotation.Entity
import groovy.transform.ToString
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

@ToString
@Entity
class CalProperties implements Serializable {

	CalPropertiesKey propertiesKey
	String propertiesValue
	String calId
	String recurrenceId
	String recurrenceIdTz

	static belongsTo = [task: TaskEntity]

	static mapping = {
		table  "cal_properties"
		version false
		columns {
			calId column: "cal_id"
			recurrenceId column: "recurrence_id"
			recurrenceIdTz column: "recurrence_id_tz"
			propertiesKey column: "key"
			propertiesValue column: "value"
			task column: "item_id"
		}
		id composite: ['task', 'propertiesKey']
		propertiesKey enumType: "identity"
	}

	static constraints = {
		recurrenceId nullable: true
		recurrenceIdTz nullable: true
	}

	boolean equals(other) {
		if (!(other instanceof CalProperties)) {
			return false
		}
		CalProperties that = (CalProperties) other
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
