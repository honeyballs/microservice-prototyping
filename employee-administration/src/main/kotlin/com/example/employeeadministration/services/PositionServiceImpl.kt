package com.example.employeeadministration.services

import com.example.employeeadministration.services.kafka.KafkaEventProducer
import com.example.employeeadministration.model.Position
import com.example.employeeadministration.model.PositionDto
import com.example.employeeadministration.model.events.AggregateState
import com.example.employeeadministration.repositories.EmployeeRepository
import com.example.employeeadministration.repositories.PositionRepository
import com.example.employeeadministration.services.kafka.SagaService
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException
import org.springframework.transaction.annotation.Transactional

@Service
class PositionServiceImpl(
        val positionRepository: PositionRepository,
        val employeeRepository: EmployeeRepository,
        val sagaService: SagaService,
        val eventProducer: KafkaEventProducer
) : PositionService {

    override fun persistWithEvents(aggregate: Position): Position {
        var agg: Position? = null
        try {
            // If id is null this is a newly created aggregate
            if (aggregate.id == null) {
                agg = positionRepository.save(aggregate)
                agg.created()
            } else {
                agg = positionRepository.save(aggregate)
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
                positionRepository.save(agg)
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

    /**
     * Return a position if present, otherwise save a new position.
     */
    @Transactional
    override fun createPositionUniquely(positionDto: PositionDto): PositionDto {
        return positionRepository.getByTitleAndDeletedFalse(positionDto.title)
                .map { mapEntityToDto(it) }
                .orElseGet { mapEntityToDto(persistWithEvents(mapDtoToEntity(positionDto))) }
    }

    @Transactional
    override fun deletePosition(id: Long) {
        if (employeeRepository.getAllByPosition_IdAndDeletedFalse(id).isEmpty()) {
            val position = positionRepository.getByIdAndDeletedFalse(id).orElseThrow {
                Exception("The job position you are trying to delete does not exist")
            }
            position.deletePosition()
            persistWithEvents(position)
        } else {
            throw Exception("The job position has employees assigned to it and cannot be deleted.")
        }
    }

    override fun mapEntityToDto(entity: Position): PositionDto {
        return PositionDto(entity.id, entity.title, entity.minHourlyWage, entity.maxHourlyWage)
    }

    override fun mapDtoToEntity(dto: PositionDto): Position {
        return Position(dto.id, dto.title, dto.minHourlyWage, dto.maxHourlyWage)
    }

    override fun mapEntitiesToDtos(entities: List<Position>): List<PositionDto> {
        return entities.map { mapEntityToDto(it) }
    }

    override fun mapDtosToEntities(dtos: List<PositionDto>): List<Position> {
        return dtos.map { mapDtoToEntity(it) }
    }
}