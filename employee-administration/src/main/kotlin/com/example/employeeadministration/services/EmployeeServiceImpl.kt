package com.example.employeeadministration.services

import com.example.employeeadministration.configurations.PendingException
import com.example.employeeadministration.configurations.throwPendingException
import com.example.employeeadministration.services.kafka.KafkaEventProducer
import com.example.employeeadministration.model.aggregates.Employee
import com.example.employeeadministration.model.dto.EmployeeDto
import com.example.employeeadministration.model.aggregates.AggregateState
import com.example.employeeadministration.repositories.DepartmentRepository
import com.example.employeeadministration.repositories.EmployeeRepository
import com.example.employeeadministration.repositories.PositionRepository
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException
import org.springframework.transaction.annotation.Transactional

@Service
class EmployeeServiceImpl(
        val employeeRepository: EmployeeRepository,
        val departmentService: DepartmentService,
        val positionService: PositionService,
        val departmentRepository: DepartmentRepository,
        val positionRepository: PositionRepository,
        val sagaService: SagaService,
        val eventProducer: KafkaEventProducer
) : EmployeeService {

    @Transactional
    override fun createEmployee(employeeDto: EmployeeDto): EmployeeDto {
        val employee = mapDtoToEntity(employeeDto)
        return mapEntityToDto(persistWithEvents(employee))
    }

    @Retryable(value = [PendingException::class], maxAttempts = 2, backoff = Backoff(700))
    @Throws(PendingException::class, Exception::class)
    override fun updateEmployee(employeeDto: EmployeeDto): EmployeeDto {
        val employee = employeeRepository.findById(employeeDto.id!!).orElseThrow()
        throwPendingException(employee)
        if (employee.firstname != employeeDto.firstname || employee.lastname != employeeDto.lastname) {
            employee.changeName(employeeDto.firstname, employeeDto.lastname)
        }
        if (employee.address != employeeDto.address) {
            employee.moveToNewAddress(employeeDto.address)
        }
        if (employee.hourlyRate != employeeDto.hourlyRate) {
            employee.receiveRaiseBy(employee.hourlyRate.minus(employeeDto.hourlyRate))
        }
        if (employee.bankDetails != employeeDto.bankDetails) {
            employee.switchBankDetails(employeeDto.bankDetails)
        }
        if (employee.department.id!! != employeeDto.department.id!!) {
            employee.moveToAnotherDepartment(departmentRepository.findById(employeeDto.department.id!!).orElseThrow())
        }
        if (employee.position.id!! != employeeDto.position.id!!) {
            employee.changeJobPosition(positionRepository.findById(employeeDto.position.id!!).orElseThrow(), null)
        }
        return mapEntityToDto(persistWithEvents(employee))
    }


    @Retryable(value = [PendingException::class], maxAttempts = 2, backoff = Backoff(700))
    @Throws(PendingException::class, Exception::class)
    @Transactional
    override fun deleteEmployee(id: Long) {
        val employee = employeeRepository.getByIdAndDeletedFalse(id).orElseThrow {
            Exception("The employee you are trying to delete does not exist")
        }
        throwPendingException(employee)
        employee.deleteEmployee()
        persistWithEvents(employee)
    }

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

    override fun mapEntityToDto(entity: Employee): EmployeeDto {
        return EmployeeDto(entity.id!!, entity.firstname, entity.lastname, entity.birthday, entity.address, entity.bankDetails, departmentService.mapEntityToDto(entity.department), positionService.mapEntityToDto(entity.position), entity.hourlyRate, entity.availableVacationHours, entity.companyMail, entity.state)
    }

    @Throws(Exception::class)
    override fun mapDtoToEntity(dto: EmployeeDto): Employee {
        val department = departmentRepository.findById(dto.department.id!!).orElseThrow()
        val position = positionRepository.findById(dto.position.id!!).orElseThrow()
        return Employee(dto.id, dto.firstname, dto.lastname, dto.birthday, dto.address, dto.bankDetails, department, position, dto.availableVacationHours, dto.hourlyRate, dto.companyMail)
    }

    override fun mapEntitiesToDtos(entities: List<Employee>): List<EmployeeDto> {
        return entities.map { mapEntityToDto(it) }
    }

    override fun mapDtosToEntities(dtos: List<EmployeeDto>): List<Employee> {
        return dtos.map { mapDtoToEntity(it) }
    }

}