package com.example.employeeadministration

import com.example.employeeadministration.model.*
import com.example.employeeadministration.repositories.EmployeeRepository
import com.example.employeeadministration.repositories.JobDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.MongoTransactionManager
import java.math.BigDecimal
import java.time.LocalDate

@SpringBootApplication
class EmployeeAdministrationApplication {


//    @Autowired
//    lateinit var jobDetailsRepository: JobDetailsRepository
//
//    @Autowired
//    lateinit var employeeRepository: EmployeeRepository
//
//    @Bean
//    fun dbInitRunner(): CommandLineRunner {
//        return CommandLineRunner {
//            val details1 = JobDetails(null, Department("Development"), Position("Junior Consultant", BigDecimal(30.00)..BigDecimal(45.00)))
//            jobDetailsRepository.save(details1)
//            val address = Address("Teststr.", 17, "Berlin", ZipCode(12345))
//            val bankDetails = BankDetails("128319815719", "4712841", "Sparkasse")
//            val employee = Employee(null, "Max", "Mustermann", LocalDate.now().minusYears(26), address, bankDetails, details1, BigDecimal(35.20), null)
//            employeeRepository.save(employee)
//        }
//    }

}

fun main(args: Array<String>) {
    runApplication<EmployeeAdministrationApplication>(*args)
}
