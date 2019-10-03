package com.example.projectadministration.services

import com.example.projectadministration.model.*
import com.example.projectadministration.repositories.CustomerRepository
import com.example.projectadministration.repositories.ProjectRepository
import com.example.projectadministration.repositories.employeeservice.EmployeeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectServiceImpl(val projectRepository: ProjectRepository, val eventProducer: EventProducer, val customerRepository: CustomerRepository, val employeeRepository: EmployeeRepository): ProjectService {

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
        val employee = entity.projectOwner
        val poDto = ProjectOwnerDto(employee.employeeId, employee.firstname, employee.lastname, employee.companyMail)
        val customerDto = ProjectCustomerDto(entity.customer.id!!, entity.customer.customerName)
        return ProjectDto(entity.id, entity.name, entity.description, entity.startDate, entity.projectedEndDate, entity.endDate, poDto, customerDto)
    }

    @Transactional
    override fun mapDtoToEntity(dto: ProjectDto): Project {
        val employee = employeeRepository.findByEmployeeId(dto.projectOwner.id).orElseThrow()
        val customer = customerRepository.findById(dto.customer.id).orElseThrow()
        return Project(dto.id, dto.name, dto.description, dto.startDate, dto.projectedEndDate, dto.endDate, employee, customer)
    }

    override fun mapEntitiesToDtos(entities: List<Project>): List<ProjectDto> {
        return entities.map { mapEntityToDto(it) }
    }

    override fun mapDtosToEntities(dtos: List<ProjectDto>): List<Project> {
        return dtos.map { mapDtoToEntity(it) }
    }

}