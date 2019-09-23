package com.example.employeeadministration

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

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

//    @Autowired
//    lateinit var departmentService: DepartmentService
//
//    @Bean
//    fun repositoryKafkaTestRunner(): CommandLineRunner {
//        return CommandLineRunner {
//            var department = Department(null, "Dev", false)
//            department = departmentService.persistWithEvents(department)
//            department.renameDepartment("HR")
//            departmentService.persistWithEvents(department)
//        }
//    }

}

fun main(args: Array<String>) {
    runApplication<EmployeeAdministrationApplication>(*args)
}
