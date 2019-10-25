package com.example.worktimeadministration

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

const val SERVICE_NAME = "WORKTIME_SERVICE"

@SpringBootApplication
class WorktimeAdministrationApplication

fun main(args: Array<String>) {
    runApplication<WorktimeAdministrationApplication>(*args)
}
