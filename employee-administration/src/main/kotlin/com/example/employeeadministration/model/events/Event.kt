package com.example.employeeadministration.model.events

/**
 * Defines the Event properties that every event needs which are an id and a timestamp.
 */
interface Event {

    val id: String
    val eventCreatedAt: String
}