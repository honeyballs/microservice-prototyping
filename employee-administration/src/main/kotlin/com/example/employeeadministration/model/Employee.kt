package com.example.employeeadministration.model

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import javax.persistence.Id

/**
 * Aggregate Employee encapsulating necessary Value Objects and handling possible invariants
 *
 */
class Employee(val id: Long?, var firstname: String, var lastname: String, val birthday: LocalDate, var address: Address, var bankDetails: BankDetails, var position: Position, hourlyRate: BigDecimal, companyMail: CompanyMail?, var department: Department) {

    // initialize it rounded. Apparently the custom setter is not applied to the initialization
    var hourlyRate: BigDecimal = hourlyRate.setScale(2, RoundingMode.HALF_UP)
        set(value) {
            // Always round the salary field
            field = value.setScale(2, RoundingMode.HALF_UP)
        }

    var companyMail = companyMail ?: CompanyMail(firstname, lastname)

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

    /**
     * Change the name(s) of a employee which subsequently changes the mail address
     */
    fun changeName(firstname: String?, lastname: String?) {
        this.firstname = firstname ?: this.firstname
        this.lastname = lastname ?: this.lastname
        this.companyMail = CompanyMail(this.firstname, this.lastname)
    }

    fun moveToAnotherDepartment(department: Department) {
        this.department = department
    }

}