package com.example.employeeadministration.services

import com.example.employeeadministration.model.DEPARTMENT_TOPIC_NAME
import com.example.employeeadministration.model.EMPLOYEE_TOPIC_NAME
import com.example.employeeadministration.model.POSITION_TOPIC_NAME
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = [EMPLOYEE_TOPIC_NAME, DEPARTMENT_TOPIC_NAME, POSITION_TOPIC_NAME])
class EventTypeHelperTests {

    val aggregate = "department"
    val action = "created"
    val eventTypeKey = "department.created"

    @Test
    fun shouldGetEventType() {
        val eventType = getEventTypeFromProperties(aggregate, action)
        Assertions.assertThat(eventType).isEqualTo("Department created")
    }

    fun shouldGetSuccessEvent() {
        val eventType = getResponseEventType(eventTypeKey, false)
        Assertions.assertThat(eventType).isEqualTo("Department created successfully")
    }

}