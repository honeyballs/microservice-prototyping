package com.example.worktimeadministration.services.kafka.project

import com.example.worktimeadministration.SERVICE_NAME
import com.example.worktimeadministration.model.aggregates.AggregateState
import com.example.worktimeadministration.model.aggregates.project.PROJECT_AGGREGATE_NAME
import com.example.worktimeadministration.model.aggregates.project.Project
import com.example.worktimeadministration.model.dto.employee.EmployeeKfk
import com.example.worktimeadministration.model.dto.project.ProjectKfk
import com.example.worktimeadministration.model.events.DomainEvent
import com.example.worktimeadministration.model.events.UpdateStateEvent
import com.example.worktimeadministration.repositories.employee.EmployeeRepository
import com.example.worktimeadministration.repositories.project.ProjectRepository
import com.example.worktimeadministration.services.EventHandler
import com.example.worktimeadministration.services.getActionOfConsumedEvent
import com.example.worktimeadministration.services.kafka.KafkaEventProducer
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException
import org.springframework.transaction.annotation.Transactional
import javax.persistence.RollbackException

@Service
@KafkaListener(groupId = SERVICE_NAME, topics = [PROJECT_AGGREGATE_NAME])
class ProjectServiceProjectKafkaEventHandler(
        val producer: KafkaEventProducer,
        val projectRepository: ProjectRepository,
        val employeeRepository: EmployeeRepository
) : EventHandler {

    val logger = LoggerFactory.getLogger(ProjectServiceProjectKafkaEventHandler::class.java)

    @KafkaHandler
    @Transactional
    fun handle(event: DomainEvent, ack: Acknowledgment) {
        logger.info("Project Event received. Type: ${event.type}, Id: ${event.to.id}")
        val action = getActionOfConsumedEvent(event.type)
        val eventProject = event.to
        try {

            when (action) {
                "created" -> createProject(eventProject as ProjectKfk)
                "updated" -> updateProject(eventProject as ProjectKfk)
                "deleted" -> deleteProject(eventProject as ProjectKfk)
                "compensation" -> compensateProject(eventProject as ProjectKfk, event.from as? ProjectKfk)
            }

            val success = event.successEvent
            success.consumerName = SERVICE_NAME
            producer.sendDomainEvent(eventProject.id, success, PROJECT_AGGREGATE_NAME)

        } catch (rollback: UnexpectedRollbackException) {
            rollback.printStackTrace()
            val failure = event.failureEvent
            failure.consumerName = SERVICE_NAME
            producer.sendDomainEvent(eventProject.id, failure, PROJECT_AGGREGATE_NAME)
        } catch (exception: Exception) {
            exception.printStackTrace()
            val failure = event.failureEvent
            failure.consumerName = SERVICE_NAME
            producer.sendDomainEvent(eventProject.id, failure, PROJECT_AGGREGATE_NAME)
        } finally {
            ack.acknowledge()
        }
    }

    @KafkaHandler
    @Transactional
    fun handle(event: UpdateStateEvent, ack: Acknowledgment) {
        logger.info("Employee Saga State Event received")
        try {
            changeAggregateState(event.aggregateId, event.state)
        } catch (rollback: UnexpectedRollbackException) {
            rollback.printStackTrace()
        } catch (exception: Exception) {
            exception.printStackTrace()
        } finally {
            ack.acknowledge()
        }
    }

    @Throws(RollbackException::class, Exception::class)
    fun createProject(eventProject: ProjectKfk) {
        val employees = employeeRepository.findAllByEmployeeIdIn(eventProject.employees.toList()).toSet()
        val proj = Project(null, eventProject.id, eventProject.name, eventProject.description, eventProject.startDate, eventProject.projectedEndDate, eventProject.endDate, employees.toMutableSet(), eventProject.deleted, eventProject.state)
        projectRepository.save(proj)
    }

    @Throws(RollbackException::class, Exception::class)
    fun updateProject(eventProject: ProjectKfk) {
        // If we would not load beforehand a new db row would be created because dbId is only set in this service
        val proj = projectRepository.findByProjectId(eventProject.id).orElseThrow()
        val employees = employeeRepository.findAllByEmployeeIdIn(eventProject.employees.toList()).toMutableSet()
        proj.name = eventProject.name
        proj.description = eventProject.description
        proj.startDate = eventProject.startDate
        proj.projectedEndDate = eventProject.projectedEndDate
        proj.endDate = eventProject.endDate
        proj.employees = employees
        proj.state = eventProject.state
        projectRepository.save(proj)
    }

    @Throws(RollbackException::class, Exception::class)
    fun deleteProject(eventProject: ProjectKfk) {
        val proj = projectRepository.findByProjectId(eventProject.id).orElseThrow()
        proj.deleted = true
        proj.state = eventProject.state
        projectRepository.save(proj)
    }

    @Throws(RollbackException::class, Exception::class)
    fun compensateProject(failedValue: ProjectKfk, compensationValue: ProjectKfk?) {
        projectRepository.findByProjectId(failedValue.id).ifPresent {
            if (compensationValue == null) {
                projectRepository.deleteById(it.dbId!!)
            } else {
                it.name = compensationValue.name
                it.description = compensationValue.description
                it.startDate = compensationValue.startDate
                it.projectedEndDate = compensationValue.projectedEndDate
                it.endDate = compensationValue.endDate
                it.employees = employeeRepository.findAllByEmployeeIdIn(compensationValue.employees.toList()).toMutableSet()
                it.deleted = compensationValue.deleted
                it.state = AggregateState.ACTIVE
                projectRepository.save(it)
            }
        }
    }

    @Throws(RollbackException::class, Exception::class)
    fun changeAggregateState(id: Long, state: AggregateState) {
        val proj = projectRepository.findByProjectId(id).orElseThrow()
        proj.state = state
        projectRepository.save(proj)
    }

    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}