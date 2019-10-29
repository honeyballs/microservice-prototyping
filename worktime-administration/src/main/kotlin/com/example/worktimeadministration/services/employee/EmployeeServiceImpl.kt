package com.example.worktimeadministration.services.employee

import com.example.worktimeadministration.model.aggregates.employee.Employee
import com.example.worktimeadministration.model.dto.employee.EmployeeDto
import com.example.worktimeadministration.repositories.employee.EmployeeRepository
import com.example.worktimeadministration.repositories.project.ProjectRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EmployeeServiceImpl(): EmployeeService {

    override fun mapEntityToDto(entity: Employee): EmployeeDto {
        return EmployeeDto(
                entity.employeeId,
                entity.firstname,
                entity.lastname
        )
    }

    override fun mapDtoToEntity(dto: EmployeeDto): Employee {
        TODO("Not Implemented because reverse mapping should never occur. No properties need to be altered")
    }

    override fun mapEntitiesToDtos(entities: List<Employee>): List<EmployeeDto> {
        return entities.map { mapEntityToDto(it) }
    }

    override fun mapDtosToEntities(dtos: List<EmployeeDto>): List<Employee> {
        TODO("Not Implemented because reverse mapping should never occur. No properties need to be altered")
    }


}