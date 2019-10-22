package com.example.projectadministration.services

import com.example.projectadministration.model.aggregates.AggregateState
import com.example.projectadministration.model.aggregates.Project
import com.example.projectadministration.model.dto.ProjectCustomerDto
import com.example.projectadministration.model.dto.ProjectDto
import com.example.projectadministration.model.dto.ProjectEmployeeDto
import com.example.projectadministration.repositories.CustomerRepository
import com.example.projectadministration.repositories.ProjectRepository
import com.example.projectadministration.repositories.employee.EmployeeRepository
import com.example.projectadministration.services.kafka.KafkaEventProducer
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectServiceImpl(
        val projectRepository: ProjectRepository,
        val customerRepository: CustomerRepository,
        val employeeRepository: EmployeeRepository,
        val sagaService: SagaService,
        val eventProducer: KafkaEventProducer): ProjectService {

    @Transactional
    override fun persistWithEvents(aggregate: Project): Project {
        var agg: Project? = null
        try {
            // If id is null this is a newly created aggregate
            if (aggregate.id == null) {
                agg = projectRepository.save(aggregate)
                agg.created()
            } else {
                agg = projectRepository.save(aggregate)
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
                projectRepository.save(agg)
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

    override fun mapEntityToDto(entity: Project): ProjectDto {
        val employeeDtos = entity.employees.map { ProjectEmployeeDto(it.employeeId, it.firstname, it.lastname, it.companyMail) }.toSet()
        val customerDto = ProjectCustomerDto(entity.customer.id!!, entity.customer.customerName)
        return ProjectDto(entity.id, entity.name, entity.description, entity.startDate, entity.projectedEndDate, entity.endDate, employeeDtos, customerDto)
    }

    @Transactional
    override fun mapDtoToEntity(dto: ProjectDto): Project {
        val employees = employeeRepository.findByEmployeeIdIn(dto.projectEmployees.map { it.id }).toSet()
        val customer = customerRepository.findById(dto.customer.id).orElseThrow()
        return Project(dto.id, dto.name, dto.description, dto.startDate, dto.projectedEndDate, dto.endDate, employees, customer)
    }

    override fun mapEntitiesToDtos(entities: List<Project>): List<ProjectDto> {
        return entities.map { mapEntityToDto(it) }
    }

    override fun mapDtosToEntities(dtos: List<ProjectDto>): List<Project> {
        return dtos.map { mapDtoToEntity(it) }
    }

}