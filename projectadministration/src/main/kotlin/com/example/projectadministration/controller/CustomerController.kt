package com.example.projectadministration.controller

import com.example.projectadministration.model.dto.CustomerDto
import org.springframework.http.ResponseEntity

interface CustomerController {

    fun getAllCustomers(): ResponseEntity<List<CustomerDto>>
    fun getCustomerById(id: Long): ResponseEntity<CustomerDto>
    fun getCustomerOfProject(projectId: Long): ResponseEntity<CustomerDto>

    fun createCustomer(customerDto: CustomerDto): ResponseEntity<CustomerDto>
    fun updateCustomer(customerDto: CustomerDto): ResponseEntity<CustomerDto>
    fun deleteCustomer(id: Long)

}