package com.example.employeeadministration.configurations

import com.example.employeeadministration.model.DEPARTMENT_TOPIC_NAME
import com.example.employeeadministration.model.EMPLOYEE_TOPIC_NAME
import com.example.employeeadministration.model.POSITION_TOPIC_NAME
import com.example.employeeadministration.model.events.DomainEvent
import com.example.employeeadministration.model.events.Event
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.IntegerSerializer
import org.apache.kafka.common.serialization.LongDeserializer
import org.apache.kafka.common.serialization.LongSerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.kafka.listener.MessageListenerContainer
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

@Configuration
@Profile("!test")
class KafkaConfiguration {

    // We have to use the spring configured object mapper which is able to map kotlin
    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var env: Environment

    @Bean
    fun employeeTopic(): NewTopic {
        return NewTopic(EMPLOYEE_TOPIC_NAME, 1, 1)
    }

    @Bean
    fun departmentTopic(): NewTopic {
        return NewTopic(DEPARTMENT_TOPIC_NAME, 1, 1)
    }

    @Bean
    fun positionTopic(): NewTopic {
        return NewTopic(POSITION_TOPIC_NAME, 1, 1)
    }

    @Bean
    fun producerConfigs():Map<String, Any> {
        val configs = HashMap<String, Any>()
        configs[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = env.getProperty("KAFKA_URL", "localhost:9092")
        return configs
    }

    @Bean
    fun admin(): KafkaAdmin {
        return KafkaAdmin(producerConfigs())
    }

    @Bean
    fun producerFactory(): ProducerFactory<Long, Event> {
        val serializer = JsonSerializer<Event>(mapper)
        serializer.isAddTypeInfo = false
        return DefaultKafkaProducerFactory<Long, Event>(producerConfigs(), LongSerializer(), serializer)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<Long, Event> {
        return KafkaTemplate<Long, Event>(producerFactory())
    }

}