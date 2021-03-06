package com.example.worktimeadministration.services.kafka.employee

import com.example.worktimeadministration.SERVICE_NAME
import com.example.worktimeadministration.model.aggregates.AggregateState
import com.example.worktimeadministration.model.aggregates.employee.EMPLOYEE_AGGREGATE_NAME
import com.example.worktimeadministration.model.aggregates.employee.Employee
import com.example.worktimeadministration.model.dto.employee.EmployeeKfk
import com.example.worktimeadministration.model.events.DomainEvent
import com.example.worktimeadministration.model.events.ResponseEvent
import com.example.worktimeadministration.model.events.UpdateStateEvent
import com.example.worktimeadministration.repositories.employee.EmployeeRepository
import com.example.worktimeadministration.services.EventHandler
import com.example.worktimeadministration.model.events.getActionOfConsumedEvent
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
@KafkaListener(groupId = SERVICE_NAME, topics = [EMPLOYEE_AGGREGATE_NAME])
class EmployeeServiceEmployeeKafkaEventHandler(
        val producer: KafkaEventProducer,
        val employeeRepository: EmployeeRepository): EventHandler {

    val logger = LoggerFactory.getLogger(EmployeeServiceEmployeeKafkaEventHandler::class.java)

    @KafkaHandler
    @Transactional
    fun handle(event: DomainEvent, ack: Acknowledgment) {
        logger.info("Employee Event received. Type: ${event.type}, Id: ${event.to.id}")
        val action = getActionOfConsumedEvent(event.type)
        val eventEmployee = event.to
//        try {

            when(action) {
                "created" -> createEmployee(eventEmployee as EmployeeKfk)
                "updated" -> updateEmployee(eventEmployee as EmployeeKfk)
                "deleted" -> deleteEmployee(eventEmployee as EmployeeKfk)
                "compensation" -> compensateEmployee(eventEmployee as EmployeeKfk, event.from as? EmployeeKfk)
            }



            val success = ResponseEvent(event.id, event.successEventType)
            success.consumerName = SERVICE_NAME
            producer.sendDomainEvent(eventEmployee.id, success, EMPLOYEE_AGGREGATE_NAME)

            ack.acknowledge()

//        } catch (rollback: UnexpectedRollbackException) {
//            rollback.printStackTrace()
//            val failure = ResponseEvent(event.id, event.failureEventType)
//            failure.consumerName = SERVICE_NAME
//            producer.sendDomainEvent(eventEmployee.id, failure, EMPLOYEE_AGGREGATE_NAME)
//        } catch (exception: Exception) {
//            exception.printStackTrace()
//            val failure = ResponseEvent(event.id, event.failureEventType)
//            failure.consumerName = SERVICE_NAME
//            producer.sendDomainEvent(eventEmployee.id, failure, EMPLOYEE_AGGREGATE_NAME)
//        } finally {
//            ack.acknowledge()
//        }
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
    fun createEmployee(eventEmployee: EmployeeKfk) {
        val emp = Employee(null, eventEmployee.id, eventEmployee.firstname, eventEmployee.lastname, eventEmployee.companyMail, eventEmployee.availableVacationHours, 0,eventEmployee.deleted, eventEmployee.state)
        employeeRepository.save(emp)
    }

    @Throws(RollbackException::class, Exception::class)
    fun updateEmployee(eventEmployee: EmployeeKfk) {
        // If we would not load beforehand a new db row would be created because dbId is only set in this service
        val emp = employeeRepository.findByEmployeeId(eventEmployee.id).orElseThrow()
        emp.firstname = eventEmployee.firstname
        emp.lastname = eventEmployee.lastname
        emp.companyMail = eventEmployee.companyMail
        emp.availableVacationHours = eventEmployee.availableVacationHours
        emp.state = eventEmployee.state
        employeeRepository.save(emp)
    }

    @Throws(RollbackException::class, Exception::class)
    fun deleteEmployee(eventEmployee: EmployeeKfk) {
        val emp = employeeRepository.findByEmployeeId(eventEmployee.id).orElseThrow()
        emp.deleted = true
        emp.state = eventEmployee.state
        employeeRepository.save(emp)
    }

    @Throws(RollbackException::class, Exception::class)
    fun compensateEmployee(failedValue: EmployeeKfk, compensationValue: EmployeeKfk?) {
        employeeRepository.findByEmployeeId(failedValue.id).ifPresent {
            if (compensationValue == null) {
                employeeRepository.deleteById(it.dbId!!)
            } else {
                it.firstname = compensationValue.firstname
                it.lastname = compensationValue.lastname
                it.companyMail = compensationValue.companyMail
                it.deleted = compensationValue.deleted
                it.state = AggregateState.ACTIVE
                employeeRepository.save(it)
            }
        }
    }

    @Throws(RollbackException::class, Exception::class)
    fun changeAggregateState(id: Long, state: AggregateState) {
        val employee = employeeRepository.findByEmployeeId(id).orElseThrow()
        employee.state = state
        employeeRepository.save(employee)
    }

    @KafkaHandler(isDefault = true)
    fun defaultHandler(message: Any) {
        println("Message received: $message")
    }

}