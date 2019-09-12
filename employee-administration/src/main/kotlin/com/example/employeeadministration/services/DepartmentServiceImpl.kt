package com.example.employeeadministration.services

import com.example.employeeadministration.model.Department
import com.example.employeeadministration.model.DepartmentDto
import com.example.employeeadministration.repositories.DepartmentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DepartmentServiceImpl(val departmentRepository: DepartmentRepository) : DepartmentService {

    /**
     * If the department already exists we just return it, otherwise it is saved and returned
     */
    @Transactional
    override fun createDepartmentUniquely(departmentDto: DepartmentDto): DepartmentDto {
        return departmentRepository.getByName(departmentDto.name)
                .map { mapEntityToDto(it) }
                .orElseGet { mapEntityToDto(departmentRepository.save(mapDtoToEntity(departmentDto))) }
    }

    override fun mapEntityToDto(entity: Department): DepartmentDto {
        return DepartmentDto(entity.id, entity.name)
    }

    override fun mapDtoToEntity(dto: DepartmentDto): Department {
        return Department(dto.id, dto.name)
    }

    override fun mapEntitiesToDtos(entities: List<Department>): List<DepartmentDto> {
        return entities.map { mapEntityToDto(it) }
    }

    override fun mapDtosToEntities(dtos: List<DepartmentDto>): List<Department> {
        return dtos.map { mapDtoToEntity(it) }
    }
}