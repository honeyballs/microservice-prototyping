package com.example.worktimeadministration.model.saga

/**
 * Defines the current state of a saga.
 */
enum class SagaState {
    RUNNING, COMPLETED, FAILED
}