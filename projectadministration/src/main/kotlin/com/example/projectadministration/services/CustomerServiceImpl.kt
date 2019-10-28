package com.example.projectadministration.services

import com.example.projectadministration.model.aggregates.AggregateState
import com.example.projectadministration.model.aggregates.Customer
import com.example.projectadministration.model.dto.CustomerDto
import com.example.projectadministration.repositories.CustomerRepository
import com.example.projectadministration.repositories.ProjectRepository
import com.example.projectadministration.services.kafka.KafkaEventProducer
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException
import org.springframework.transaction.annotation.Transactional

@Service
class CustomerServiceImpl(
        val customerRepository: CustomerRepository,
        val projectRepository: ProjectRepository,
        val sagaService: SagaService,
        val eventProducer: KafkaEventProducer
): CustomerService {

    @Throws(Exception::class)
    override fun createCustomer(customerDto: CustomerDto): CustomerDto {
        val customer = mapDtoToEntity(customerDto)
        return mapEntityToDto(persistWithEvents(customer))
    }

    @Throws(Exception::class)
    override fun updateCustomer(customerDto: CustomerDto): CustomerDto {
        val customer = customerRepository.findById(customerDto.id!!).orElseThrow()
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

    @Transactional
    override fun deleteCustomer(id: Long) {
        if (projectRepository.getAllByCustomerIdAndDeletedFalse(id).isEmpty()) {
            val customer = customerRepository.getByIdAndDeletedFalse(id).orElseThrow {
                Exception("The customer you are trying to delete does not exist")
            }
            customer.deleteCustomer()
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
                    sagaService.createSagaOfEvent(it, agg.id!!, responseEvents)
                    canBeMadeActive = false
                }
            }

            // If no saga was necessary the aggregate can immediately become active
            if (canBeMadeActive) {
                agg.state = AggregateState.ACTIVE
                customerRepository.save(agg)
            }

            // Send all events
            eventProducer.sendEventsOfAggregate(aggregate)

        } catch (rollback: UnexpectedRollbackException) {
            rollback.printStackTrace()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            return agg ?: aggregate
        }
    }

    override fun mapEntityToDto(entity: Customer): CustomerDto {
        return CustomerDto(entity.id, entity.customerName, entity.address, entity.contact)
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