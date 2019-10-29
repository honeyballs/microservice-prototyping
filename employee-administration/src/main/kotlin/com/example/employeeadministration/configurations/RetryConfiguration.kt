package com.example.employeeadministration.configurations

import com.example.employeeadministration.model.aggregates.AggregateState
import com.example.employeeadministration.model.aggregates.EventAggregate
import org.springframework.context.annotation.Configuration
import org.springframework.retry.annotation.EnableRetry

/**
 * Defines an Exception to throw when an aggregate cannot be modified because it's in a PENDING state.
 */
class PendingException(override val message: String): Exception() {
}

@Configuration
@EnableRetry
class RetryConfig {

}

/**
 * This function is used to throw an exception when an aggregate cannot be modified because it is in a pending state.
 */
@Throws(PendingException::class)
fun throwPendingException(aggregate: EventAggregate) {
    if (aggregate.state == AggregateState.PENDING) {
        throw PendingException("The Aggregate is still pending")
    }
}