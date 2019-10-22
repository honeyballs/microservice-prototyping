package com.example.employeeadministration

import com.example.employeeadministration.model.aggregates.Department
import com.example.employeeadministration.model.aggregates.Employee
import com.example.employeeadministration.model.aggregates.Position
import com.example.employeeadministration.model.valueobjects.Address
import com.example.employeeadministration.model.valueobjects.BankDetails
import com.example.employeeadministration.model.valueobjects.ZipCode
import com.example.employeeadministration.services.DepartmentService
import com.example.employeeadministration.services.EmployeeService
import com.example.employeeadministration.services.PositionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.math.BigDecimal
import java.time.LocalDate

const val SERVICE_NAME = "EMPLOYEE_SERVICE"

@SpringBootApplication
class EmployeeAdministrationApplication {

//    @Autowired
//    lateinit var eventProducer: EventProducer
//
//    @Bean
//    fun kafkaTestRunner(): CommandLineRunner {
//        return CommandLineRunner {
//            val comp = DepartmentCreatedCompensation(1L)
//            val dep = Department(1L, "Development")
//            eventProducer.sendDomainEvent(DepartmentCreatedEvent(dep, comp))
//        }
//    }
//
    @Autowired
    lateinit var departmentService: DepartmentService

    @Autowired
    lateinit var positionService: PositionService

    @Autowired
    lateinit var employeeService: EmployeeService

    @Bean
    fun repositoryKafkaTestRunner(): CommandLineRunner {
        return CommandLineRunner {
            var department = Department(null, "Dev", false)
            department = departmentService.persistWithEvents(department)
            var position = Position(null, "Developer", BigDecimal(30.20), BigDecimal(50.12))
            position = positionService.persistWithEvents(position)
            val address = Address("Teststr.", 17, "Berlin", ZipCode(12345))
            val bankDetails = BankDetails("128319815719", "4712841", "Sparkasse")
            var employee = Employee(null, "Max", "Mustermann", LocalDate.now().minusYears(25), address, bankDetails, department, position, BigDecimal(40.34), null)
            employee = employeeService.persistWithEvents(employee)
//            department.renameDepartment("Java Development")
//            departmentService.persistWithEvents(department)
//            position.changePositionTitle("Java Developer")
//            positionService.persistWithEvents(position)
        }
    }

}

fun main(args: Array<String>) {
    runApplication<EmployeeAdministrationApplication>(*args)
}
