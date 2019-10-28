package com.example.employeeadministration.controllers

import com.example.employeeadministration.model.dto.PositionDto
import org.springframework.http.ResponseEntity
import java.math.BigDecimal


interface PositionController {

    fun getAllPositions(): ResponseEntity<List<PositionDto>>
    fun getPositionById(id: Long): ResponseEntity<PositionDto>

    fun createPosition(positionDto: PositionDto): ResponseEntity<PositionDto>
    fun updatePosition(positionDto: PositionDto): ResponseEntity<PositionDto>
    fun deletePosition(id: Long)

}