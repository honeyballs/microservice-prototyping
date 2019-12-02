package com.example.employeeadministration.model.events

import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.support.PropertiesLoaderUtils
import java.util.*
import kotlin.NoSuchElementException

/**
 * Contains helper functions to extract event types from a properties file
 */

/**
 * Load an event Type for the specified aggregate from a properties file.
 */
fun getEventTypeFromProperties(aggregate: String, action: String): String {
    return getEventTypeProperties()["$aggregate.$action"] as String
}

/**
 * Get either the success or fail event belonging to the specified event.
 */
fun getResponseEventType(eventType: String, fail: Boolean): String {
    val producerProps = getEventTypeProperties()
    val consumerProps = getConsumerEventTypes()
    if (fail) {
        return consumerProps["${getKeyFromEventType(producerProps, eventType)}.fail"] as String
    } else {
        return consumerProps["${getKeyFromEventType(producerProps, eventType)}.success"] as String
    }
}

/**
 * Pass a response event to receive the response keyword (e.g. "success", "fail")
 */
fun getResponseEventKeyword(eventType: String): String {
    val props = getConsumerEventTypes()
    val key = getKeyFromEventType(props, eventType)
    return key.substringAfterLast(".")
}

/**
 * Get the action part of an response event key.
 */
fun getResponseEventAction(eventType: String): String {
    val props = getEventTypeProperties()
    val key = getKeyFromEventType(props, eventType)
    return key.substringBeforeLast(".")
}

/**
 * Retrieve a response event type other than the basic success/fail
 */
fun getSpecificEventResponseType(eventType: String, responseType: String): String {
    val props = getEventTypeProperties()
    return props["${getKeyFromEventType(props, eventType)}.$responseType"] as String
}

/**
 * Retrieve the event to send when a saga is complete for an aggregate.
 */
fun getSagaCompleteType(aggregateName: String): String {
    val props = getEventTypeProperties()
    return props["$aggregateName.completed"] as String
}

/**
 * Pass an event to extract a comma separated list of services which need to respond to the given event.
 */
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

/**
 * Extracts the action of a consumed event to determine what to do.
 */
fun getActionOfConsumedEvent(eventType: String): String {
    val key = getKeyFromEventType(getConsumerEventTypes(), eventType)
    return key.substringAfterLast(".")
}

/**
 * Load the properties file containing all event types this service sends.
 */
fun getEventTypeProperties(): Properties {
    val res = ClassPathResource("/event-types.properties")
    return PropertiesLoaderUtils.loadProperties(res)
}

/**
 * Load the properties file containing the services which have to respond to events.
 */
fun getRequiredSuccessProperties(): Properties {
    val res = ClassPathResource("/saga.properties")
    return PropertiesLoaderUtils.loadProperties(res)
}

/**
 * Load the properties file containing events this service consumes.
 */
fun getConsumerEventTypes(): Properties {
    val res = ClassPathResource("/consume-types.properties")
    return PropertiesLoaderUtils.loadProperties(res)
}

/**
 * Helper functions to get the property key to a passed event type.
 */
fun getKeyFromEventType(props: Properties, eventType: String): String {
    val entries = props.entries
    val entry = entries.first {
        it.value == eventType
    }
    return entry.key as String
}