package com.example.employeeadministration.services

import com.example.employeeadministration.model.DEPARTMENT_AGGREGATE_NAME
import com.example.employeeadministration.model.EMPLOYEE_AGGREGATE_NAME
import com.example.employeeadministration.model.POSITION_AGGREGATE_NAME
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
@EmbeddedKafka(partitions = 1, topics = [EMPLOYEE_AGGREGATE_NAME, DEPARTMENT_AGGREGATE_NAME, POSITION_AGGREGATE_NAME])
class EventTypeHelperTests {

    val aggregate = "department"
    val action = "created"
    val eventTypeKey = "department.created"

    @Test
    fun shouldGetEventType() {
        val eventType = getEventTypeFromProperties(aggregate, action)
        Assertions.assertThat(eventType).isEqualTo("Department created")
    }

    @Test
    fun shouldGetSuccessEvent() {
        val eventType = getResponseEventType("Department created", false)
        Assertions.assertThat(eventType).isEqualTo("Department created successfully")
    }

    @Test
    fun shouldGetEmptyWhenAccessingNonExistingResponseEvents() {
        Assertions.assertThat(getRequiredSuccessEvents("asdf")).isEqualTo("")
    }

    @Test
    fun shouldGetResponseKeyword() {
        val event = "Employee deletion failed"
        Assertions.assertThat(getResponseEventKeyword(event)).isEqualTo("fail")
    }

    @Test
    fun shouldGetCompletionType() {
        Assertions.assertThat(getSagaCompleteType(DEPARTMENT_AGGREGATE_NAME)).isEqualTo("Department saga completed")
    }

}