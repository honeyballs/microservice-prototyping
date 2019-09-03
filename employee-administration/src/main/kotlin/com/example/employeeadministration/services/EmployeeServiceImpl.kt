package com.example.employeeadministration.services

import com.example.employeeadministration.model.Employee
import com.example.employeeadministration.model.EmployeeDto
import org.springframework.stereotype.Service

@Service
class EmployeeServiceImpl : EmployeeService {

    override fun mapEntityToDto(entity: Employee): EmployeeDto {
        return EmployeeDto(entity.id, entity.firstname, entity.lastname, entity.birthday, entity.address, entity.bankDetails, entity.jobDetails, entity.hourlyRate, entity.companyMail)
    }

    override fun mapDtoToEntity(dto: EmployeeDto): Employee {
        return Employee(dto.id, dto.firstname, dto.lastname, dto.birthday, dto.address, dto.bankDetails, dto.jobDetails, dto.hourlyRate, dto.companyMail)
    }

    override fun mapEntitiesToDtos(entities: List<Employee>): List<EmployeeDto> {
        return entities.map { mapEntityToDto(it) }
    }

    override fun mapDtosToEntities(dtos: List<EmployeeDto>): List<Employee> {
        return dtos.map { mapDtoToEntity(it) }
    }

}