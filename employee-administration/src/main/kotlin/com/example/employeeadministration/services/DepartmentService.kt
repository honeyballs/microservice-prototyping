package com.example.employeeadministration.services

import com.example.employeeadministration.model.aggregates.Department
import com.example.employeeadministration.model.dto.DepartmentDto

interface DepartmentService : MappingService<Department, DepartmentDto>, EventProducingPersistenceService<Department> {

    fun getAllDepartments(): List<DepartmentDto>
    fun getDepartmentById(id: Long): DepartmentDto
    fun createDepartmentUniquely(departmentDto: DepartmentDto): DepartmentDto
    fun updateDepartment(departmentDto: DepartmentDto): DepartmentDto
    fun deleteDepartment(id: Long)

}