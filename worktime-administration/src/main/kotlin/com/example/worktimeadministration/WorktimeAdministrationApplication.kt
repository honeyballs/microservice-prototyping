package com.example.worktimeadministration

import com.example.worktimeadministration.model.aggregates.AggregateState
import com.example.worktimeadministration.model.aggregates.employee.Employee
import com.example.worktimeadministration.repositories.employee.EmployeeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

const val SERVICE_NAME = "WORKTIME_SERVICE"

@SpringBootApplication
class WorktimeAdministrationApplication {

//    @Autowired
//    lateinit var employeeRepository: EmployeeRepository
//
//    @Bean
//    fun runner(): CommandLineRunner {
//        return CommandLineRunner {
//            var employee = Employee(null, 3, "Max", "Mustermann", "m.mustermann@company.com", 240, 0, false, AggregateState.ACTIVE)
//            employee = employeeRepository.save(employee)
//        }
//    }
}

fun main(args: Array<String>) {
    runApplication<WorktimeAdministrationApplication>(*args)
}
