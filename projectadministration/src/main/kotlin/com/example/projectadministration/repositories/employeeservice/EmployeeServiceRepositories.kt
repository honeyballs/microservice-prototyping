package com.example.projectadministration.repositories.employeeservice

import com.example.projectadministration.model.employee.Department
import com.example.projectadministration.model.employee.Employee
import com.example.projectadministration.model.employee.Position
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository: JpaRepository<Employee, Long> {
}

@Repository
interface PositionRepository: JpaRepository<Position, Long> {

}

@Repository
interface DepartmentRepository: JpaRepository<Department, Long> {

}