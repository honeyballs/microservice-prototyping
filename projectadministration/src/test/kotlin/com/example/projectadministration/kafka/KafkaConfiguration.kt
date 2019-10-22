package com.example.projectadministration.kafka

import com.example.projectadministration.model.aggregates.employee.DEPARTMENT_AGGREGATE_NAME
import com.example.projectadministration.model.aggregates.employee.EMPLOYEE_AGGREGATE_NAME
import com.example.projectadministration.model.aggregates.employee.POSITION_AGGREGATE_NAME
import com.example.projectadministration.model.events.Event
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.LongDeserializer
import org.apache.kafka.common.serialization.LongSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.kafka.test.EmbeddedKafkaBroker

const val TOPIC_NAME = "employee"

@Configuration
@EnableKafka
@Profile("test")
class KafkaConfiguration {

    // We have to use the spring configured object mapper which is able to map kotlin
    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker

    @Bean
    fun employeeTopic(): NewTopic {
        return NewTopic(EMPLOYEE_AGGREGATE_NAME, 1, 1)
    }

    @Bean
    fun departmentTopic(): NewTopic {
        return NewTopic(DEPARTMENT_AGGREGATE_NAME, 1, 1)
    }

    @Bean
    fun positionTopic(): NewTopic {
        return NewTopic(POSITION_AGGREGATE_NAME, 1, 1)
    }

    @Bean
    fun producerConfigs():Map<String, Any> {
        val configs = HashMap<String, Any>()
        configs[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = embeddedKafkaBroker.brokersAsString
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

    @Bean
    fun consumerConfig(): Map<String, Any> {
        val configs = HashMap<String, Any>()
        configs[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = embeddedKafkaBroker.brokersAsString
        configs[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        configs[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = "false"
        return configs
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<Long, Event> {
        val deserializer = JsonDeserializer<Event>(Event::class.java, mapper)
        //deserializer.addTrustedPackages("com.example")
        deserializer.setUseTypeHeaders(false)
        return DefaultKafkaConsumerFactory<Long, Event>(consumerConfig(), LongDeserializer(), deserializer)
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<Long, Event> {
        val factory = ConcurrentKafkaListenerContainerFactory<Long, Event>()
        factory.consumerFactory = consumerFactory()
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
        return factory
    }

}