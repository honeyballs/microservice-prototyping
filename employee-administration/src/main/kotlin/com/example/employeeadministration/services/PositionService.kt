package com.example.employeeadministration.services

import com.example.employeeadministration.model.Position
import com.example.employeeadministration.model.PositionDto

interface PositionService : MappingService<Position, PositionDto> {

    fun createPositionUniquely(position: Position): PositionDto

}