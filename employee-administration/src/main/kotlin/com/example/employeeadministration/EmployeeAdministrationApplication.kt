package com.example.employeeadministration

import com.example.employeeadministration.kafka.EventProducer
import com.example.employeeadministration.model.Department
import com.example.employeeadministration.model.events.DepartmentCreatedCompensation
import com.example.employeeadministration.model.events.DepartmentCreatedEvent
import com.example.employeeadministration.model.events.DomainEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

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

}

fun main(args: Array<String>) {
    runApplication<EmployeeAdministrationApplication>(*args)
}
