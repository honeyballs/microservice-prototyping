package com.example.projectadministration.services

import com.example.projectadministration.model.Customer
import com.example.projectadministration.model.CustomerDto
import org.springframework.stereotype.Service

@Service
class CustomerServiceImpl: CustomerService {

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