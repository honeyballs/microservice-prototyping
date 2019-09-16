package com.example.employeeadministration.configurations

import com.example.employeeadministration.model.events.DomainEvent
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
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.kafka.listener.MessageListenerContainer
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

const val TOPIC_NAME = "employee"

@Configuration
@EnableKafka
class KafkaConfiguration {

    // We have to use the spring configured object mapper which is able to map kotlin
    @Autowired
    lateinit var mapper: ObjectMapper

    @Bean
    fun employeeTopic(): NewTopic {
        return NewTopic(TOPIC_NAME, 1, 1)
    }

    @Bean
    fun producerConfigs():Map<String, Any> {
        val configs = HashMap<String, Any>()
        configs[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        return configs
    }

    @Bean
    fun producerFactory(): ProducerFactory<Long, DomainEvent> {
        return DefaultKafkaProducerFactory<Long, DomainEvent>(producerConfigs(), LongSerializer(), JsonSerializer<DomainEvent>(mapper))
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<Long, DomainEvent> {
        return KafkaTemplate<Long, DomainEvent>(producerFactory())
    }

    @Bean
    fun consumerConfig(): Map<String, Any> {
        val configs = HashMap<String, Any>()
        configs[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        return configs
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<Long, DomainEvent> {
        return DefaultKafkaConsumerFactory<Long, DomainEvent>(consumerConfig(), LongDeserializer(), JsonDeserializer<DomainEvent>(DomainEvent::class.java, mapper))
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<Long, DomainEvent> {
        val factory = ConcurrentKafkaListenerContainerFactory<Long, DomainEvent>()
        factory.consumerFactory = consumerFactory()
        return factory
    }

}