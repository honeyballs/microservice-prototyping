package com.example.employeeadministration.controllers

import com.example.employeeadministration.model.dto.PositionDto
import com.example.employeeadministration.repositories.PositionRepository
import com.example.employeeadministration.services.PositionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal

const val positionUrl = "position"

@RestController
class PositionControllerImpl(val positionService: PositionService, val positionRepository: PositionRepository): PositionController {

    @GetMapping(positionUrl)
    override fun getAllPositions(): ResponseEntity<List<PositionDto>> {
      return ok(positionService.getAllPositions())
    }

    @GetMapping("$positionUrl/{id}")
    override fun getPositionById(@PathVariable("id") id: Long): ResponseEntity<PositionDto> {
        try {
            return ok(positionService.getPositionById(id))
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find position using the given id")
        }
    }

    @PostMapping(positionUrl)
    override fun createPosition(@RequestBody positionDto: PositionDto): ResponseEntity<PositionDto> {
        return ok(positionService.createPositionUniquely(positionDto))
    }

    @PutMapping("$positionUrl")
    override fun updatePosition(@RequestBody positionDto: PositionDto): ResponseEntity<PositionDto> {
        try {
            return ok(positionService.updatePosition(positionDto))
        } catch (ex: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong when updating the position", ex)
        }
    }

    @DeleteMapping(positionUrl)
    override fun deletePosition(id: Long) {
        try {
            positionService.deletePosition(id)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }
}