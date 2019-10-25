package com.example.worktimeadministration.services

import com.example.worktimeadministration.model.aggregates.WorktimeEntry
import com.example.worktimeadministration.model.dto.WorktimeEntryDto

interface WorktimeEntryService: MappingService<WorktimeEntry, WorktimeEntryDto>, EventProducingPersistenceService<WorktimeEntry> {
}