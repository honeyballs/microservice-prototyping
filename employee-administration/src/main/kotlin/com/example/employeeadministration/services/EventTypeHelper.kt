package com.example.employeeadministration.services

import org.apache.kafka.common.protocol.types.Field
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.support.PropertiesLoaderUtils
import java.util.*

/**
 * Contains helper functions to extract event types from a properties file
 */

fun getEventTypeFromProperties(aggregate: String, action: String): String {
    return getEventTypeProperties()["$aggregate.$action"] as String
}

fun getResponseEventType(eventType: String, fail: Boolean): String {
    val props = getEventTypeProperties()
    if (fail) {
        return props["${getKeyFromEventType(props, eventType)}.fail"] as String
    } else {
        return props["${getKeyFromEventType(props, eventType)}.success"] as String
    }
}

fun getSpecificEventResponseType(eventType: String, responseType: String): String {
    val props = getEventTypeProperties()
    return props["${getKeyFromEventType(props, eventType)}.$responseType"] as String
}

fun getRequiredSuccessEvents(eventType: String): Int {
    val typeProps = getEventTypeProperties()
    val props = getRequiredSuccessProperties()
    return (props[getKeyFromEventType(typeProps, eventType)] as String).toInt()
}

fun getEventTypeProperties(): Properties {
    val res = ClassPathResource("/event-types.properties")
    return PropertiesLoaderUtils.loadProperties(res)
}

fun getRequiredSuccessProperties(): Properties {
    val res = ClassPathResource("/saga.properties")
    return PropertiesLoaderUtils.loadProperties(res)
}

fun getKeyFromEventType(props: Properties, eventType: String): String {
    val entries = props.entries
    val entry = entries.first {
        it.value == eventType
    }
    return entry.key as String
}