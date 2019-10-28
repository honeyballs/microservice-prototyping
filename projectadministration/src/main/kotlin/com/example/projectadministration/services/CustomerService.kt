package com.example.projectadministration.services

import com.example.projectadministration.model.aggregates.Customer
import com.example.projectadministration.model.dto.CustomerDto

interface CustomerService: MappingService<Customer, CustomerDto>, EventProducingPersistenceService<Customer> {

    fun createCustomer(customerDto: CustomerDto): CustomerDto
    fun updateCustomer(customerDto: CustomerDto): CustomerDto
    fun deleteCustomer(id: Long)

}