package com.example.employeeadministration.controllers

import com.example.employeeadministration.model.PositionDto
import com.example.employeeadministration.repositories.PositionRepository
import com.example.employeeadministration.services.PositionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal

const val positionUrl = "positions"

@RestController
class PositionControllerImpl(val positionService: PositionService, val positionRepository: PositionRepository): PositionController {

    @GetMapping(positionUrl)
    override fun getAllPositions(): ResponseEntity<List<PositionDto>> {
      return ok(positionRepository.findAll().map { positionService.mapEntityToDto(it) })
    }

    @GetMapping("$positionUrl/{id}")
    override fun getPositionById(@PathVariable("id") id: Long): ResponseEntity<PositionDto> {
        return ok(positionRepository.getById(id).map { positionService.mapEntityToDto(it) }.orElseThrow {
            ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find position using the given id")
        })
    }

    @PostMapping(positionUrl)
    override fun createPosition(@RequestBody positionDto: PositionDto): ResponseEntity<PositionDto> {
        return ok(positionService.createPositionUniquely(positionDto))
    }

    @PutMapping("$positionUrl/{id}/title")
    override fun updatePositionTitle(@PathVariable("id") id: Long, @RequestParam("title") title: String): ResponseEntity<PositionDto> {
        return ok(positionRepository.getById(id).map {
            it.changePositionTitle(title)
            positionService.mapEntityToDto(positionRepository.save(it))
        }.orElseThrow {
            ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find position to update")
        })
    }

    @PutMapping("$positionUrl/{id}/range")
    override fun updatePositionWageRange(@PathVariable("id") id: Long, @RequestParam("min") min: BigDecimal, @RequestParam("max") max: BigDecimal): ResponseEntity<PositionDto> {
        return ok(positionRepository.getById(id).map {
            it.adjustWageRange(min, max)
            positionService.mapEntityToDto(positionRepository.save(it))
        }.orElseThrow {
            ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find position to update")
        })
    }

    @DeleteMapping(positionUrl)
    override fun deletePosition(id: Long): ResponseEntity<PositionDto> {
        val position = positionRepository.getById(id).orElseThrow {
            ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find position to delete")
        }
        positionRepository.deleteById(id)
        return ok(positionService.mapEntityToDto(position))
    }
}