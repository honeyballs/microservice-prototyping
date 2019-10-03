package com.example.projectadministration.model.events

import com.example.projectadministration.model.Project
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName


@JsonTypeName("projectCompensation")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
class ProjectCompensation(val project: Project, type: EventType): CompensatingAction(type)

@JsonTypeName("projectEvent")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
class ProjectEvent(val project: Project, compensatingAction: ProjectCompensation, type: EventType): DomainEvent(compensatingAction, type)