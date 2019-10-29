package com.example.projectadministration

import com.example.projectadministration.model.aggregates.*
import com.example.projectadministration.model.aggregates.employee.Department
import com.example.projectadministration.model.aggregates.employee.Employee
import com.example.projectadministration.model.aggregates.employee.Position
import com.example.projectadministration.repositories.CustomerRepository
import com.example.projectadministration.repositories.employee.DepartmentRepository
import com.example.projectadministration.repositories.employee.EmployeeRepository
import com.example.projectadministration.repositories.employee.PositionRepository
import com.example.projectadministration.services.ProjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

const val SERVICE_NAME = "PROJECT_SERVICE"

@SpringBootApplication
class ProjectAdministrationApplication {

//    @Autowired
//    lateinit var departmentRepository: DepartmentRepository
//
//    @Autowired
//    lateinit var employeeRepository: EmployeeRepository
//
//    @Autowired
//    lateinit var positionRepository: PositionRepository
//
//    @Autowired
//    lateinit var customerRepository: CustomerRepository
//
//    @Autowired
//    lateinit var projectService: ProjectService
//
//    @Bean
//    fun runner(): CommandLineRunner {
//
//        return CommandLineRunner {
//            var department = Department(null,  1, "Java Development", false, AggregateState.ACTIVE)
//            department = departmentRepository.save(department)
//            var position = Position(null, 2, "Developer", false, AggregateState.ACTIVE)
//            position = positionRepository.save(position)
//            var employee = Employee(null, 3, "Max", "Mustermann", department, position, "m.mustermann@company.com", false, AggregateState.ACTIVE)
//            employee = employeeRepository.save(employee)
//            var customer = Customer(null, "CSTMER",
//                    Address("street", 2, "city", ZipCode(12345)),
//                    CustomerContact("Dude", "Dudeson", "sadasd@asda.com", "1418209481024"))
//            customer.state = AggregateState.ACTIVE
//            customerRepository.save(customer)
//            var project = Project(null, "Testproject", "asdasdasd", LocalDate.now(), LocalDate.now().plusMonths(6), null, mutableSetOf(employee), customer)
//            projectService.persistWithEvents(project)
//        }
//    }

}

fun main(args: Array<String>) {
    runApplication<ProjectAdministrationApplication>(*args)
}
