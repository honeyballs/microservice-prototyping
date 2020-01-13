package com.example.employeeadministration.services

import com.example.employeeadministration.configurations.PendingException
import com.example.employeeadministration.configurations.throwPendingException
import com.example.employeeadministration.services.kafka.KafkaEventProducer
import com.example.employeeadministration.model.aggregates.Position
import com.example.employeeadministration.model.dto.PositionDto
import com.example.employeeadministration.model.aggregates.AggregateState
import com.example.employeeadministration.model.events.getRequiredSuccessEvents
import com.example.employeeadministration.repositories.EmployeeRepository
import com.example.employeeadministration.repositories.PositionRepository
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException
import org.springframework.transaction.annotation.Transactional
import java.lang.RuntimeException

@Service
class PositionServiceImpl(
        val positionRepository: PositionRepository,
        val employeeRepository: EmployeeRepository,
        val sagaService: SagaService,
        val eventProducer: KafkaEventProducer
) : PositionService {

    override fun getAllPositions(): List<PositionDto> {
        return positionRepository.getAllByDeletedFalse().map { mapEntityToDto(it) }
    }

    override fun getPositionById(id: Long): PositionDto {
        return positionRepository.getByIdAndDeletedFalse(id).map { mapEntityToDto(it) }.orElseThrow()
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
    @Retryable(value = [PendingException::class], maxAttempts = 2, backoff = Backoff(700))
    @Throws(PendingException::class, Exception::class)
    override fun updatePosition(positionDto: PositionDto): PositionDto {
        val position = positionRepository.findById(positionDto.id!!).orElseThrow()
        throwPendingException(position)
        if (position.title != positionDto.title) {
            position.changePositionTitle(positionDto.title)
        }
        if (position.minHourlyWage != positionDto.minHourlyWage || position.maxHourlyWage != positionDto.maxHourlyWage) {
            position.adjustWageRange(positionDto.minHourlyWage, positionDto.maxHourlyWage)
        }
        return mapEntityToDto(persistWithEvents(position))
    }

    @Retryable(value = [PendingException::class], maxAttempts = 2, backoff = Backoff(700))
    @Throws(PendingException::class, Exception::class)
    @Transactional
    override fun deletePosition(id: Long) {
        if (employeeRepository.getAllByPosition_IdAndDeletedFalse(id).isEmpty()) {
            val position = positionRepository.getByIdAndDeletedFalse(id).orElseThrow {
                Exception("The job position you are trying to delete does not exist")
            }
            throwPendingException(position)
            position.deleteAggregate()
            persistWithEvents(position)
        } else {
            throw Exception("The job position has employees assigned to it and cannot be deleted.")
        }
    }

    @Transactional
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
                    sagaService.createSagaOfEvent(it, agg.id!!, responseEvents, null)
                    canBeMadeActive = false
                }
            }

            // If no saga was necessary the aggregate can immediately become active
            if (canBeMadeActive) {
                agg.state = AggregateState.ACTIVE
                positionRepository.save(agg)
            }

            // Send all events
            // Send all events. If this fails we initiate a rollback
            try {
                eventProducer.sendEventsOfAggregate(aggregate)
            } catch (e: Exception) {
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

    override fun mapEntityToDto(entity: Position): PositionDto {
        return PositionDto(entity.id!!, entity.title, entity.minHourlyWage, entity.maxHourlyWage, entity.state)
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