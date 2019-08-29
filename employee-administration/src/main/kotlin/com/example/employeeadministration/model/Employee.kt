package com.example.employeeadministration.model

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import javax.persistence.Id

/**
 * Entity representing an employee.
 *
 */
class Employee private constructor(val id: Long?, var firstname: String, var lastname: String, val birthday: LocalDate, var address: Address, var bankDetails: BankDetails, var position: Position) {

    var hourlyRate: BigDecimal = position.baseHourlyWageRange.start
        set(value) {
            // Always round the salary field
            field = value.setScale(2, RoundingMode.HALF_UP)
        }

    constructor(id: Long?, firstname: String, lastname: String, birthday: LocalDate, address: Address, bankDetails: BankDetails, position: Position, hourlyRate: BigDecimal): this(id, firstname, lastname, birthday, address, bankDetails, position) {
        this.hourlyRate = hourlyRate
    }

    override fun toString(): String {
        return "$lastname, $firstname, born on: ${birthday.toString()} - Position: ${position.toString()}"
    }

    fun moveToNewAddress(address: Address) {
        this.address = address
    }

    fun receiveRaiseBy(raiseAmount: BigDecimal) {
        this.hourlyRate = hourlyRate.add(raiseAmount)
    }

    fun switchBankDetails(bankDetails: BankDetails) {
        this.bankDetails = bankDetails
    }

    /**
     * Changes the position of an employy
     *
     * @param newSalary If no salary is provided the mininum of the provided position is used
     */
    fun changeJobPosition(position: Position, newSalary: BigDecimal?) {
        this.position = position
        this.hourlyRate = newSalary ?: position.baseHourlyWageRange.start
    }

}