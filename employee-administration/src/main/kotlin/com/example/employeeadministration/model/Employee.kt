package com.example.employeeadministration.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

/**
 * Aggregate Employee encapsulating necessary Value Objects and handling possible invariants
 *
 */
class Employee(@Id var id: String?, var firstname: String, var lastname: String, val birthday: LocalDate, var address: Address, var bankDetails: BankDetails, @DBRef var jobDetails: JobDetails, hourlyRate: BigDecimal, companyMail: CompanyMail?) {

    // initialize it rounded. Apparently the custom setter is not applied to the initialization
    var hourlyRate: BigDecimal = hourlyRate.setScale(2, RoundingMode.HALF_UP)
        set(value) {
            // Always round the salary field
            field = value.setScale(2, RoundingMode.HALF_UP)
        }

    var companyMail = companyMail ?: CompanyMail(firstname, lastname)

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
     * Changes the position of an employee
     *
     * @param newSalary If no salary is provided the mininum of the provided position is used
     */
    fun changeJobPosition(jobDetails: JobDetails, newSalary: BigDecimal?) {
        this.jobDetails = jobDetails
        this.hourlyRate = newSalary ?: jobDetails.position.baseHourlyWageRange.start
    }

    fun moveToAnotherDepartment(jobDetails: JobDetails) {
        this.jobDetails = jobDetails
    }

    /**
     * Change the name(s) of a employee which subsequently changes the mail address
     */
    fun changeName(firstname: String?, lastname: String?) {
        this.firstname = firstname ?: this.firstname
        this.lastname = lastname ?: this.lastname
        this.companyMail = CompanyMail(this.firstname, this.lastname)
    }

    override fun toString(): String {
        return "$lastname, $firstname, born on: ${birthday.toString()} - Position: ${jobDetails.position.toString()}"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Employee) return false
        return this.id == other.id
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + firstname.hashCode()
        result = 31 * result + lastname.hashCode()
        result = 31 * result + birthday.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + bankDetails.hashCode()
        result = 31 * result + jobDetails.hashCode()
        result = 31 * result + hourlyRate.hashCode()
        result = 31 * result + companyMail.hashCode()
        return result
    }


}