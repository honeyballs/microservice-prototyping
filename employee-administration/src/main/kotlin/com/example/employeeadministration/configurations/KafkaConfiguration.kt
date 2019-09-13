package com.example.employeeadministration.configurations

import com.example.employeeadministration.model.events.DomainEvent
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.IntegerSerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

const val TOPIC_NAME = "employee"

@Configuration
class KafkaConfiguration {

    @Bean
    fun employeeTopic(): NewTopic {
        return NewTopic(TOPIC_NAME, 1, 1)
    }

    @Bean
    fun producerConfigs():Map<String, Any> {
        val configs = HashMap<String, Any>()
        configs[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        configs[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configs[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        return configs
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, DomainEvent> {
        return DefaultKafkaProducerFactory<String, DomainEvent>(producerConfigs())
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, DomainEvent> {
        return KafkaTemplate<String, DomainEvent>(producerFactory())
    }

}