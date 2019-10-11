package com.example.employeeadministration.kafka

import com.example.employeeadministration.model.aggregates.*
import com.example.employeeadministration.model.valueobjects.Address
import com.example.employeeadministration.model.valueobjects.BankDetails
import com.example.employeeadministration.model.valueobjects.ZipCode
import com.example.employeeadministration.repositories.DepartmentRepository
import com.example.employeeadministration.repositories.EmployeeRepository
import com.example.employeeadministration.repositories.PositionRepository
import com.example.employeeadministration.services.kafka.KafkaDepartmentEventHandler
import com.example.employeeadministration.services.kafka.KafkaEventProducer
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.math.BigDecimal
import java.time.LocalDate

@RunWith(SpringRunner::class)
@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = [EMPLOYEE_AGGREGATE_NAME, DEPARTMENT_AGGREGATE_NAME, POSITION_AGGREGATE_NAME])
class KafkaApplicationTests {

    @Autowired
    lateinit var producer: KafkaEventProducer

    @Autowired
    lateinit var consumer: KafkaDepartmentEventHandler

    @Autowired
    lateinit var departmentRepository: DepartmentRepository

    @Autowired
    lateinit var positionRepository: PositionRepository

    @Autowired
    lateinit var employeeRepository: EmployeeRepository

    lateinit var department: Department
    lateinit var position: Position
    lateinit var employee: Employee

    @Before
    fun setup() {
        department = Department(null, "Development")
        department = departmentRepository.save(department)
        position = Position(null, "Development", BigDecimal(30.20), BigDecimal(40.50))
        position = positionRepository.save(position)
        val startSalary = BigDecimal(40.50565)
        val address = Address("Teststr.", 17, "Berlin", ZipCode(12345))
        val bankDetails = BankDetails("128319815719", "4712841", "Sparkasse")
        employee = Employee(null, "Max", "Mustermann", LocalDate.now().minusYears(26), address, bankDetails, department, position, startSalary, null)
        employee = employeeRepository.save(employee)
    }

//
//    @Test
//    fun shouldSendDepartmentCompensation() {
//        val comp = DepartmentCompensation(department.mapAggregateToKafkaDto(), EventType.CREATE)
//        producer.sendDomainEvent(department.id!!, comp, DEPARTMENT_TOPIC_NAME)
//        Thread.sleep(1000)
//        val deletedDepartment = departmentRepository.findById(department.id!!).get()
//        Assertions.assertThat(deletedDepartment.deleted).isTrue()
//    }
//
//    @Test
//    fun shouldSendPositionCompensation() {
//        val comp = PositionCompensation(position.mapAggregateToKafkaDto(), EventType.UPDATE)
//        position.title = "Java Development"
//        positionRepository.save(position)
//        producer.sendDomainEvent(position.id!!, comp, POSITION_TOPIC_NAME)
//        Thread.sleep(1000)
//        val pos = positionRepository.findById(position.id!!).get()
//        Assertions.assertThat(pos.title).isEqualTo("Development")
//    }
//
//    @Test
//    fun shouldSendEmployeeCompensation() {
//        employee.deleted = true
//        employeeRepository.save(employee)
//        employee.deleted = false
//        val comp = EmployeeCompensation(employee.mapAggregateToKafkaDto(), EventType.DELETE)
//        producer.sendDomainEvent(employee.id!!, comp, EMPLOYEE_TOPIC_NAME)
//        Thread.sleep(1000)
//        employee = employeeRepository.getByIdAndDeletedFalse(employee.id!!).orElseThrow()
//        Assertions.assertThat(employee.deleted).isFalse()
//    }
//


}