package com.example.projectadministration.services

import com.example.projectadministration.configurations.PendingException
import com.example.projectadministration.configurations.throwPendingException
import com.example.projectadministration.model.aggregates.AggregateState
import com.example.projectadministration.model.aggregates.Customer
import com.example.projectadministration.model.dto.CustomerDto
import com.example.projectadministration.model.events.getRequiredSuccessEvents
import com.example.projectadministration.repositories.CustomerRepository
import com.example.projectadministration.repositories.ProjectRepository
import com.example.projectadministration.services.kafka.KafkaEventProducer
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException
import org.springframework.transaction.annotation.Transactional
import java.lang.RuntimeException

@Service
class CustomerServiceImpl(
        val customerRepository: CustomerRepository,
        val projectRepository: ProjectRepository,
        val sagaService: SagaService,
        val eventProducer: KafkaEventProducer
): CustomerService {

    override fun getAllCustomers(): List<CustomerDto> {
        return customerRepository.getAllByDeletedFalse().map { mapEntityToDto(it) }
    }

    override fun getCustomerById(id: Long): CustomerDto {
        return customerRepository.getByIdAndDeletedFalse(id).map { mapEntityToDto(it) }.orElseThrow()
    }

    override fun getCustomerOfProject(projectId: Long): CustomerDto {
        return projectRepository.getByIdAndDeletedFalse(projectId).map { mapEntityToDto(it.customer) }.orElseThrow()
    }

    @Throws(Exception::class)
    @Transactional
    override fun createCustomer(customerDto: CustomerDto): CustomerDto {
        val customer = mapDtoToEntity(customerDto)
        return mapEntityToDto(persistWithEvents(customer))
    }

    @Retryable(value = [PendingException::class], maxAttempts = 2, backoff = Backoff(700))
    @Throws(PendingException::class, Exception::class)
    @Transactional
    override fun updateCustomer(customerDto: CustomerDto): CustomerDto {
        val customer = customerRepository.findById(customerDto.id!!).orElseThrow()
        throwPendingException(customer)
        if (customer.customerName != customerDto.customerName) {
            customer.changeName(customerDto.customerName)
        }
        if (customer.address != customerDto.address) {
            customer.moveCompanyLocation(customerDto.address)
        }
        if (customer.contact != customerDto.contact) {
            customer.changeCustomerContact(customerDto.contact)
        }
        return mapEntityToDto(persistWithEvents(customer))
    }

    @Retryable(value = [PendingException::class], maxAttempts = 2, backoff = Backoff(700))
    @Throws(PendingException::class, Exception::class)
    @Transactional
    override fun deleteCustomer(id: Long) {
        if (projectRepository.getAllByCustomerIdAndDeletedFalse(id).isEmpty()) {
            val customer = customerRepository.getByIdAndDeletedFalse(id).orElseThrow {
                Exception("The customer you are trying to delete does not exist")
            }
            throwPendingException(customer)
            customer.deleteAggregate()
            persistWithEvents(customer)
        } else {
            throw Exception("The customer has projects assigned to it and cannot be deleted.")
        }
    }

    @Transactional
    override fun persistWithEvents(aggregate: Customer): Customer {
        var agg: Customer? = null
        try {
            // If id is null this is a newly created aggregate
            if (aggregate.id == null) {
                agg = customerRepository.save(aggregate)
                agg.created()
            } else {
                agg = customerRepository.save(aggregate)
            }

            // If services must send response events to changes in the aggregate we create a saga
            var canBeMadeActive = true
            aggregate.events()!!.second.forEach {
                val responseEvents = getRequiredSuccessEvents(it.type)
                if (responseEvents != "") {
                    sagaService.createSagaOfEvent(it, agg.id!!, responseEvents, null)
                    canBeMadeActive = false
                }
            }

            // If no saga was necessary the aggregate can immediately become active
            if (canBeMadeActive) {
                agg.state = AggregateState.ACTIVE
                customerRepository.save(agg)
            }

            // Send all events. If this failes we initiate a rollback
            try {
                eventProducer.sendEventsOfAggregate(aggregate)
            } catch (e: java.lang.Exception) {
                // Runtime Exceptions initiate a rollback when thrown in a method annotated with @Transactional
                throw RuntimeException("Events could not be sent")
            }

        } catch (rollback: UnexpectedRollbackException) {
            rollback.printStackTrace()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            return agg ?: aggregate
        }
    }

    override fun mapEntityToDto(entity: Customer): CustomerDto {
        return CustomerDto(entity.id, entity.customerName, entity.address, entity.contact, entity.state)
    }

    override fun mapDtoToEntity(dto: CustomerDto): Customer {
        return Customer(dto.id, dto.customerName, dto.address, dto.contact)
    }

    override fun mapEntitiesToDtos(entities: List<Customer>): List<CustomerDto> {
        return entities.map { mapEntityToDto(it) }
    }

    override fun mapDtosToEntities(dtos: List<CustomerDto>): List<Customer> {
        return dtos.map { mapDtoToEntity(it) }
    }

}