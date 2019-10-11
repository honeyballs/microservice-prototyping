package com.example.employeeadministration.services

import com.example.employeeadministration.services.kafka.KafkaEventProducer
import com.example.employeeadministration.model.Employee
import com.example.employeeadministration.model.EmployeeDto
import com.example.employeeadministration.model.events.AggregateState
import com.example.employeeadministration.repositories.EmployeeRepository
import com.example.employeeadministration.services.kafka.SagaService
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException
import org.springframework.transaction.annotation.Transactional

@Service
class EmployeeServiceImpl(
        val employeeRepository: EmployeeRepository,
        val departmentService: DepartmentService,
        val positionService: PositionService,
        val sagaService: SagaService,
        val eventProducer: KafkaEventProducer
) : EmployeeService {

    override fun persistWithEvents(aggregate: Employee): Employee {
        var agg: Employee? = null
        try {
            // If id is null this is a newly created aggregate
            if (aggregate.id == null) {
                agg = employeeRepository.save(aggregate)
                agg.created()
            } else {
                agg = employeeRepository.save(aggregate)
            }

            // If services must send response events to changes in the aggregate we create a saga
            var canBeMadeActive = true
            aggregate.events()!!.second.forEach {
                val responseEvents = getRequiredSuccessEvents(it.type)
                if (responseEvents != "") {
                    sagaService.createSagaOfEvent(it, agg.id!!, responseEvents)
                    canBeMadeActive = false
                }
            }

            // If no saga was necessary the aggregate can immediately become active
            if (canBeMadeActive) {
                agg.state = AggregateState.ACTIVE
                employeeRepository.save(agg)
            }

            // Send all events
            eventProducer.sendEventsOfAggregate(aggregate)
        } catch (rollback: UnexpectedRollbackException) {
            rollback.printStackTrace()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            return agg ?: aggregate
        }
    }

    @Transactional
    override fun deleteEmployee(id: Long) {
        val employee = employeeRepository.getByIdAndDeletedFalse(id).orElseThrow {
            Exception("The employee you are trying to delete does not exist")
        }
        employee.deleteEmployee()
        persistWithEvents(employee)
    }

    override fun mapEntityToDto(entity: Employee): EmployeeDto {
        return EmployeeDto(entity.id, entity.firstname, entity.lastname, entity.birthday, entity.address, entity.bankDetails, departmentService.mapEntityToDto(entity.department), positionService.mapEntityToDto(entity.position), entity.hourlyRate, entity.companyMail)
    }

    override fun mapDtoToEntity(dto: EmployeeDto): Employee {
        return Employee(dto.id, dto.firstname, dto.lastname, dto.birthday, dto.address, dto.bankDetails, departmentService.mapDtoToEntity(dto.department), positionService.mapDtoToEntity(dto.position), dto.hourlyRate, dto.companyMail)
    }

    override fun mapEntitiesToDtos(entities: List<Employee>): List<EmployeeDto> {
        return entities.map { mapEntityToDto(it) }
    }

    override fun mapDtosToEntities(dtos: List<EmployeeDto>): List<Employee> {
        return dtos.map { mapDtoToEntity(it) }
    }

}