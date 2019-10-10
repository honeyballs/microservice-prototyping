package com.example.employeeadministration.model.events

import com.example.employeeadministration.model.*
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import org.apache.kafka.common.protocol.types.Field


//@JsonTypeName("employeeEvent")
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
//class EmployeeEvent(val employee: EmployeeKfk, type: String): DomainEvent(type)
//
//
//
//@JsonTypeName("departmentEvent")
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
//class DepartmentEvent(val department: DepartmentKfk, type: String): DomainEvent(type)
//
//@JsonTypeName("positionEvent")
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
//class PositionEvent(val position: PositionKfk, type: String): DomainEvent(type)