package com.example.employeeadministration.services

import com.example.employeeadministration.services.kafka.KafkaEventProducer
import com.example.employeeadministration.model.Department
import com.example.employeeadministration.model.DepartmentDto
import com.example.employeeadministration.repositories.DepartmentRepository
import com.example.employeeadministration.repositories.EmployeeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException
import org.springframework.transaction.annotation.Transactional

@Service
class DepartmentServiceImpl(val departmentRepository: DepartmentRepository,
                            val employeeRepository: EmployeeRepository,
                            val eventProducer: KafkaEventProducer) : DepartmentService {

    override fun persistWithEvents(aggregate: Department): Department {
        var agg: Department? = null
        try {
            // If id is null this is a newly created aggregate
            if (aggregate.id == null) {
                agg = departmentRepository.save(aggregate)
                agg.created()
            } else {
                agg = departmentRepository.save(aggregate)
            }
            eventProducer.sendEventsOfAggregate(aggregate)
        } catch (rollback: UnexpectedRollbackException) {
            rollback.printStackTrace()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            return agg ?: aggregate
        }
    }

    /**
     * If the department already exists we just return it, otherwise it is saved and returned
     */
    @Transactional
    override fun createDepartmentUniquely(departmentDto: DepartmentDto): DepartmentDto {
        return departmentRepository.getByNameAndDeletedFalse(departmentDto.name)
                .map { mapEntityToDto(it) }
                .orElseGet { mapEntityToDto(persistWithEvents(mapDtoToEntity(departmentDto))) }
    }

    @Transactional
    override fun deleteDepartment(id: Long) {
        if (employeeRepository.getAllByDepartment_IdAndDeletedFalse(id).isEmpty()) {
            val department = departmentRepository.getByIdAndDeletedFalse(id).orElseThrow {
                Exception("The department you are trying to delete does not exist")
            }
            department.deleteDepartment()
            persistWithEvents(department)
        } else {
            throw Exception("The department has employees assigned to it and cannot be deleted.")
        }
    }

    override fun mapEntityToDto(entity: Department): DepartmentDto {
        return DepartmentDto(entity.id, entity.name)
    }

    override fun mapDtoToEntity(dto: DepartmentDto): Department {
        return Department(dto.id, dto.name)
    }

    override fun mapEntitiesToDtos(entities: List<Department>): List<DepartmentDto> {
        return entities.map { mapEntityToDto(it) }
    }

    override fun mapDtosToEntities(dtos: List<DepartmentDto>): List<Department> {
        return dtos.map { mapDtoToEntity(it) }
    }
}