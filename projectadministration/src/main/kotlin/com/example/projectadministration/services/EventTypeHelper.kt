package com.example.projectadministration.services

import org.apache.kafka.common.protocol.types.Field
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.support.PropertiesLoaderUtils
import java.util.*
import kotlin.NoSuchElementException

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

fun getResponseEventKeyword(eventType: String): String {
    val props = getEventTypeProperties()
    val key = getKeyFromEventType(props, eventType)
    return key.substringAfterLast(".")
}

fun getSpecificEventResponseType(eventType: String, responseType: String): String {
    val props = getEventTypeProperties()
    return props["${getKeyFromEventType(props, eventType)}.$responseType"] as String
}

fun getSagaCompleteType(aggregateName: String): String {
    val props = getEventTypeProperties()
    return props["$aggregateName.completed"] as String
}

fun getRequiredSuccessEvents(eventType: String): String {
    val typeProps = getEventTypeProperties()
    val props = getRequiredSuccessProperties()
    var eventsString = ""
    try {
        val events = props[getKeyFromEventType(typeProps, eventType)] as String?
        eventsString = events ?: ""
    } catch (ex: NoSuchElementException) {
        ex.printStackTrace()
    }
    return eventsString
}

fun getActionOfConsumedEvent(eventType: String): String {
    val key = getKeyFromEventType(getConsumerEventTypes(), eventType)
    return key.substringAfterLast(".")
}

fun getEventTypeProperties(): Properties {
    val res = ClassPathResource("/event-types.properties")
    return PropertiesLoaderUtils.loadProperties(res)
}

fun getRequiredSuccessProperties(): Properties {
    val res = ClassPathResource("/saga.properties")
    return PropertiesLoaderUtils.loadProperties(res)
}

fun getConsumerEventTypes(): Properties {
    val res = ClassPathResource("/consume-types.properties")
    return PropertiesLoaderUtils.loadProperties(res)
}

fun getKeyFromEventType(props: Properties, eventType: String): String {
    val entries = props.entries
    val entry = entries.first {
        it.value == eventType
    }
    return entry.key as String
}