package com.example.projectadministration

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

const val SERVICE_NAME = "PROJECT_SERVICE"

@SpringBootApplication
class ProjectAdministrationApplication

fun main(args: Array<String>) {
    runApplication<ProjectAdministrationApplication>(*args)
}
