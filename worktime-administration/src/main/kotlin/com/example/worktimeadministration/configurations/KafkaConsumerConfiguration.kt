package com.example.worktimeadministration.configurations

import com.example.worktimeadministration.SERVICE_NAME
import com.example.worktimeadministration.model.aggregates.employee.EMPLOYEE_AGGREGATE_NAME
import com.example.worktimeadministration.model.aggregates.project.PROJECT_AGGREGATE_NAME
import com.example.worktimeadministration.model.dto.BaseKfkDto
import com.example.worktimeadministration.model.dto.employee.EmployeeKfk
import com.example.worktimeadministration.model.dto.project.ProjectKfk
import com.example.worktimeadministration.model.events.DomainEvent
import com.example.worktimeadministration.model.events.Event
import com.example.worktimeadministration.model.events.ResponseEvent
import com.example.worktimeadministration.services.kafka.KafkaEventProducer
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.LongDeserializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.adapter.RetryingMessageListenerAdapter
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.transaction.KafkaTransactionManager
import org.springframework.retry.RecoveryCallback
import org.springframework.retry.backoff.BackOffPolicy
import org.springframework.retry.backoff.ExponentialBackOffPolicy
import org.springframework.retry.backoff.FixedBackOffPolicy
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate

@Configuration
@EnableKafka
@Profile("!test")
class KafkaConsumerConfiguration {

    // We have to use the spring configured object mapper which is able to map kotlin
    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var env: Environment

    @Bean
    fun consumerConfig(): Map<String, Any> {
        val configs = HashMap<String, Any>()
        configs[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = env.getProperty("KAFKA_URL", "localhost:9093")
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
    fun kafkaListenerContainerFactory(kafkaTransactionManager: KafkaTransactionManager<Long, Event>, kafkaRecoveryCallback: RecoveryCallback<Unit>): ConcurrentKafkaListenerContainerFactory<Long, Event> {
        val factory = ConcurrentKafkaListenerContainerFactory<Long, Event>()
        factory.consumerFactory = consumerFactory()
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
        factory.setConcurrency(1)
        val retryTemplate = RetryTemplate()
        retryTemplate.setBackOffPolicy(FixedBackOffPolicy())
        retryTemplate.setRetryPolicy(SimpleRetryPolicy(5))
        factory.setRetryTemplate(retryTemplate)
        factory.setRecoveryCallback(kafkaRecoveryCallback)
        // The transaction manager is created in the producer configuration
        // When so configured, the container starts a transaction before invoking the listener.
        // Any KafkaTemplate operations performed by the listener participate in the transaction.
        // https://docs.spring.io/spring-kafka/reference/html/#transaction-synchronization
        factory.containerProperties.transactionManager = kafkaTransactionManager
        return factory
    }

    @Bean
    fun kafkaRecoveryCallback(producer: KafkaEventProducer): RecoveryCallback<Unit> {
        return RecoveryCallback {
            println("=== RECOVERY CALLBACK IN ACTION ===")
            val record: ConsumerRecord<Long, Event>? = it.getAttribute(RetryingMessageListenerAdapter.CONTEXT_RECORD) as? ConsumerRecord<Long, Event>
            val acknowledgment = it.getAttribute(RetryingMessageListenerAdapter.CONTEXT_ACKNOWLEDGMENT) as Acknowledgment
            val event = record?.value()
            if (event != null && event is DomainEvent) {
                val failure = ResponseEvent(event.id, event.failureEventType)
                failure.consumerName = SERVICE_NAME
                producer.sendDomainEvent(event.to.id, failure, decideAggregate(event.to))
                println("=== WILL SEND TO ${decideAggregate(event.to)}")
            }
            acknowledgment.acknowledge()
        }
    }

    private fun decideAggregate(kfkDto: BaseKfkDto): String {
        if (kfkDto is EmployeeKfk) {
            return EMPLOYEE_AGGREGATE_NAME
        } else if (kfkDto is ProjectKfk) {
            return PROJECT_AGGREGATE_NAME
        }
        return ""
    }
}