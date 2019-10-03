package com.example.projectadministration.services

import com.example.projectadministration.model.Customer
import com.example.projectadministration.model.CustomerDto

interface CustomerService: MappingService<Customer, CustomerDto> {


}