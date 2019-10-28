package com.example.employeeadministration.services

import com.example.employeeadministration.model.aggregates.Position
import com.example.employeeadministration.model.dto.PositionDto

interface PositionService : MappingService<Position, PositionDto>, EventProducingPersistenceService<Position> {

    fun createPositionUniquely(positionDto: PositionDto): PositionDto
    fun updatePosition(positionDto: PositionDto): PositionDto
    fun deletePosition(id: Long)

}