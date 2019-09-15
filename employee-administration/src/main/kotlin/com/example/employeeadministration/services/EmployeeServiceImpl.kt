package com.example.employeeadministration.services

import com.example.employeeadministration.model.Employee
import com.example.employeeadministration.model.EmployeeDto
import com.example.employeeadministration.repositories.EmployeeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EmployeeServiceImpl(val employeeRepository: EmployeeRepository, val departmentService: DepartmentService, val positionService: PositionService) : EmployeeService {

    @Transactional
    override fun deleteEmployee(id: Long) {
            val employee = employeeRepository.getById(id).orElseThrow {
                Exception("The employee you are trying to delete does not exist")
            }
            employee.deleteEmployee()
            employeeRepository.save(employee)
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