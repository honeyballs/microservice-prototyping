package com.example.projectadministration.services.employee

import com.example.projectadministration.model.aggregates.employee.Employee
import com.example.projectadministration.model.dto.EmployeeDto
import org.springframework.stereotype.Service

@Service
class EmployeeServiceImpl: EmployeeService {

    override fun mapEntityToDto(entity: Employee): EmployeeDto {
        return EmployeeDto(entity.employeeId, entity.firstname, entity.lastname, entity.department.name, entity.position.title, entity.companyMail)
    }

    override fun mapDtoToEntity(dto: EmployeeDto): Employee {
        TODO("Not required")
    }

    override fun mapEntitiesToDtos(entities: List<Employee>): List<EmployeeDto> {
        return entities.map { mapEntityToDto(it) }
    }

    override fun mapDtosToEntities(dtos: List<EmployeeDto>): List<Employee> {
        TODO("not required")
    }
}