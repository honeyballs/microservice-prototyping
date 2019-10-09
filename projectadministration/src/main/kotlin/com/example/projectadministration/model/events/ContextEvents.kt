package com.example.projectadministration.model.events

import com.example.projectadministration.model.CustomerKfk
import com.example.projectadministration.model.Project
import com.example.projectadministration.model.ProjectKfk
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName


@JsonTypeName("projectCompensation")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
class ProjectCompensation(val project: ProjectKfk, type: EventType): CompensatingAction(type)

@JsonTypeName("projectEvent")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
class ProjectEvent(val project: ProjectKfk, compensatingAction: ProjectCompensation, type: EventType): DomainEvent(compensatingAction, type)

@JsonTypeName("customerCompensation")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
class CustomerCompensation(val customer: CustomerKfk, type: EventType): CompensatingAction(type)

@JsonTypeName("customerEvent")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
class CustomerEvent(val project: CustomerKfk, compensatingAction: CustomerCompensation, type: EventType): DomainEvent(compensatingAction, type)