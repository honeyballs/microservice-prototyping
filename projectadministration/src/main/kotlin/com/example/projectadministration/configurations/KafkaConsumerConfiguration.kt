package com.example.projectadministration.configurations

import com.example.projectadministration.model.events.Event
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.LongDeserializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.serializer.JsonDeserializer

@Configuration
@EnableKafka
@Profile("!test")
class KafkaConsumerConfiguration {

    // We have to use the spring configured object mapper which is able to map kotlin
    @Autowired
    lateinit var mapper: ObjectMapper

    @Bean
    fun consumerConfig(): Map<String, Any> {
        val configs = HashMap<String, Any>()
        configs[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
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
        factory.containerProperties.ackMode = ContainerProperties.AckMode.RECORD
        return factory
    }

}