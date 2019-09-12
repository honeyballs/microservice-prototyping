package com.example.employeeadministration.controllers

import com.example.employeeadministration.model.PositionDto
import org.springframework.http.ResponseEntity
import java.math.BigDecimal


interface PositionController {

    fun getAllPositions(): ResponseEntity<List<PositionDto>>
    fun getPositionById(id: Long): ResponseEntity<PositionDto>

    fun createPosition(positionDto: PositionDto): ResponseEntity<PositionDto>
    fun updatePositionTitle(id: Long, title: String): ResponseEntity<PositionDto>
    fun updatePositionWageRange(id: Long, min: BigDecimal, max: BigDecimal): ResponseEntity<PositionDto>
    fun deletePosition(id: Long): ResponseEntity<PositionDto>

}