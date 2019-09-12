package com.example.employeeadministration.services

import com.example.employeeadministration.model.Department
import com.example.employeeadministration.model.DepartmentDto
import com.example.employeeadministration.model.Position
import com.example.employeeadministration.model.PositionDto

interface DepartmentService : MappingService<Department, DepartmentDto> {

    fun createDepartmentUniquely(departmentDto: DepartmentDto): DepartmentDto

}