package com.example.employeeadministration.model.events

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

open class DomainEvent @JsonCreator constructor(
        @JsonProperty("id") val id: String,
        @JsonProperty("createdAt") val createdAt: String,
        @JsonProperty("action") val action: String,
        @JsonProperty("body") val body: Any) {

    constructor(action: String, body: Any): this(UUID.randomUUID().toString(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss:SSS")), action, body)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DomainEvent) return false
        if (action != other.action) return false
        if (id != other.id) return false
        if (createdAt != other.createdAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = action.hashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }


}