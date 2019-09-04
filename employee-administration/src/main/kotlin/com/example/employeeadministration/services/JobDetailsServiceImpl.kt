package com.example.employeeadministration.services

import com.example.employeeadministration.model.Department
import com.example.employeeadministration.model.JobDetails
import com.example.employeeadministration.model.JobDetailsDto
import com.example.employeeadministration.model.Position
import com.example.employeeadministration.repositories.JobDetailsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class JobDetailsServiceImpl(val jobDetailsRepository: JobDetailsRepository) : JobDetailsService {

    @Transactional
    override fun uniquelySaveJobDetails(details: JobDetails): JobDetailsDto {
        return mapEntityToDto(jobDetailsRepository.getByDepartmentAndPosition(details.department, details.position)
                .orElse(jobDetailsRepository.save(details)))
    }

    @Transactional
    override fun addPositionToDepartment(department: Department, position: Position): JobDetailsDto {
        val jobDetails = JobDetails(null, department, position)
        return uniquelySaveJobDetails(jobDetails)
    }

    override fun mapEntityToDto(entity: JobDetails): JobDetailsDto {
        return JobDetailsDto(entity.id, entity.department, entity.position)
    }

    override fun mapDtoToEntity(dto: JobDetailsDto): JobDetails {
        return JobDetails(dto.id, dto.department, dto.position)
    }

    override fun mapEntitiesToDtos(entities: List<JobDetails>): List<JobDetailsDto> {
        return entities.map { mapEntityToDto(it) }
    }

    override fun mapDtosToEntities(dtos: List<JobDetailsDto>): List<JobDetails> {
        return dtos.map { mapDtoToEntity(it) }
    }

}