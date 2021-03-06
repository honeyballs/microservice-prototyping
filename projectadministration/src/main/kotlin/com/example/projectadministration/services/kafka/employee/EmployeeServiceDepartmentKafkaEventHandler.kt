package com.example.projectadministration.services.kafka.employee

import com.example.projectadministration.SERVICE_NAME
import com.example.projectadministration.model.aggregates.AggregateState
import com.example.projectadministration.model.aggregates.employee.DEPARTMENT_AGGREGATE_NAME
import com.example.projectadministration.model.aggregates.employee.Department
import com.example.projectadministration.services.EventHandler
import com.example.projectadministration.model.dto.employee.DepartmentKfk
import com.example.projectadministration.model.events.*
import com.example.projectadministration.repositories.employee.DepartmentRepository
import com.example.projectadministration.model.events.getActionOfConsumedEvent
import com.example.projectadministration.services.kafka.KafkaEventProducer
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException
import org.springframework.transaction.annotation.Transactional
import javax.persistence.RollbackException

@Service
@KafkaListener(groupId = SERVICE_NAME, topics = [DEPARTMENT_AGGREGATE_NAME])
class EmployeeServiceDepartmentKafkaEventHandler(
        val producer: KafkaEventProducer,
        val departmentRepository: DepartmentRepository): EventHandler {

    val logger = LoggerFactory.getLogger(EmployeeServiceDepartmentKafkaEventHandler::class.java)

    @KafkaHandler
    @Transactional
    fun handle(event: DomainEvent, ack: Acknowledgment) {
        logger.info("Department Event received. Type: ${event.type}, Id: ${event.to.id}")
        val action = getActionOfConsumedEvent(event.type)
        val eventDepartment = event.to
//        try {

            when(action) {
                "created" -> createDepartment(eventDepartment as DepartmentKfk)
                "updated" -> updateDepartment(eventDepartment as DepartmentKfk)
                "deleted" -> deleteDepartment(eventDepartment as DepartmentKfk)
                "compensation" -> compensateDepartment(eventDepartment as DepartmentKfk, event.from as? DepartmentKfk)
            }

            val success = ResponseEvent(event.id, event.successEventType)
            success.consumerName = SERVICE_NAME
            producer.sendDomainEvent(eventDepartment.id, success, DEPARTMENT_AGGREGATE_NAME)
            ack.acknowledge()

//        } catch (rollback: UnexpectedRollbackException) {
//            rollback.printStackTrace()
//            val failure = ResponseEvent(event.id, event.failureEventType)
//            failure.consumerName = SERVICE_NAME
//            producer.sendDomainEvent(eventDepartment.id, failure, DEPARTMENT_AGGREGATE_NAME)
//        } catch (exception: Exception) {
//            exception.printStackTrace()
//            val failure = ResponseEvent(event.id, event.failureEventType)
//            failure.consumerName = SERVICE_NAME
//            producer.sendDomainEvent(eventDepartment.id, failure, DEPARTMENT_AGGREGATE_NAME)
//        } finally {
//            ack.acknowledge()
//        }
    }

    @KafkaHandler
    @Transactional
    fun handle(event: UpdateStateEvent, ack: Acknowledgment) {
        logger.info("Department Saga State Event received")
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
    fun createDepartment(eventDep: DepartmentKfk) {
        val department = Department(null, eventDep.id, eventDep.name, eventDep.deleted, eventDep.state)
        departmentRepository.save(department)
    }

    @Throws(RollbackException::class, Exception::class)
    fun updateDepartment(eventDep: DepartmentKfk) {
        val department = departmentRepository.findByDepartmentId(eventDep.id).orElseThrow()
        department.name = eventDep.name
        department.state = eventDep.state
        departmentRepository.save(department)
    }

    @Throws(RollbackException::class, Exception::class)
    fun deleteDepartment(eventDep: DepartmentKfk) {
        val department = departmentRepository.findByDepartmentId(eventDep.id).orElseThrow()
        department.deleted = true
        department.state = eventDep.state
        departmentRepository.save(department)
    }

    @Throws(RollbackException::class, Exception::class)
    fun compensateDepartment(failedValue: DepartmentKfk, compensationValue: DepartmentKfk?) {
        departmentRepository.findByDepartmentId(failedValue.id).ifPresent {
            if (compensationValue == null) {
                departmentRepository.deleteById(it.dbId!!)
            } else {
                it.name = compensationValue.name
                it.deleted = compensationValue.deleted
                it.state = AggregateState.ACTIVE
                departmentRepository.save(it)
            }
        }
    }

    @Throws(RollbackException::class, Exception::class)
    fun changeAggregateState(id: Long, state: AggregateState) {
        val department = departmentRepository.findByDepartmentId(id).orElseThrow()
        department.state = state
        departmentRepository.save(department)
    }

    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}