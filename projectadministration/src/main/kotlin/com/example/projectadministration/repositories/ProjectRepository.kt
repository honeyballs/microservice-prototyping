package com.example.projectadministration.repositories

import com.example.projectadministration.model.Project
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProjectRepository: JpaRepository<Project, Long> {

    fun getAllByDeletedFalse(): List<Project>
    fun getByIdAndDeletedFalse(id: Long): Optional<Project>
    fun getAllByCustomer_IdAndDeletedFalse(id: Long): List<Project>
    fun getAllByEndDateNotNullAndDeletedFalse(): List<Project>
    fun getAllByEndDateNullAndDeletedFalse(): List<Project>

}