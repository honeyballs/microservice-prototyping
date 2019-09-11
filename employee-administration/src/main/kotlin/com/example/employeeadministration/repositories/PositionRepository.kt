package com.example.employeeadministration.repositories

import com.example.employeeadministration.model.Position
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PositionRepository : JpaRepository<Position, Long> {

    fun getById(id: Long): Optional<Position>
    fun getByTitle(title: String): Optional<Position>

}