package com.example.employeeadministration.repositories

import com.example.employeeadministration.model.Employee
import org.springframework.data.mongodb.repository.MongoRepository

interface EmployeeRepository : MongoRepository<Employee, String> {

}