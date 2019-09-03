package com.example.employeeadministration.model

import java.lang.Exception
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable

/**
 * Wrapper class for ZipCode which checks if the provided number matches the required length.
 * Not sure if necessary for DDD or if validation suffices.
 *
 */
@Embeddable
data class ZipCode(val zip: Int) {

    companion object {
        val ALLOWED_LENGTHS_PER_COUNTRY = hashMapOf<Locale, Int>(Pair(Locale.GERMANY, 5))
    }

    init {
        if (zip.toString().length != ALLOWED_LENGTHS_PER_COUNTRY[Locale.GERMANY]) throw Exception("The zip code provided does not match the required length of ${ALLOWED_LENGTHS_PER_COUNTRY[Locale.GERMANY]} digits.")
    }

}

/**
 * Value Object representing an address.
 */
@Embeddable
data class Address(val street: String, val no: Int, val city: String, val zipCode: ZipCode)

/**
 * Value Object representing bank details.
 */
@Embeddable
data class BankDetails(val iban: String, val bic: String, val bankName: String)

/**
 * Value Object representing the position an employee has in the company.
 * Should this be a VO? hourly wage could be mutable.
 */
@Embeddable
class Position(val title: String, minHourlyWage: BigDecimal, maxHourlyWage: BigDecimal) {
    val minHourlyWage: BigDecimal = minHourlyWage.setScale(2, RoundingMode.HALF_UP)
    val maxHourlyWage: BigDecimal = maxHourlyWage.setScale(2, RoundingMode.HALF_UP)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Position

        if (title != other.title) return false
        if (minHourlyWage != other.minHourlyWage) return false
        if (maxHourlyWage != other.maxHourlyWage) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + minHourlyWage.hashCode()
        result = 31 * result + maxHourlyWage.hashCode()
        return result
    }


}

/**
 * Function to check whether a rate is within the limits of a job position
 */
fun Position.isRateInRange(rateToCheck: BigDecimal): Boolean {
    return rateToCheck in this.minHourlyWage..this.maxHourlyWage
}

/**
 * The Department VO provides a grouping mechanism for employee aggregates
 */
@Embeddable
data class Department(@Column(name = "department_name") val name: String)

/**
 * The mail vo provides means to create a (unique) company mail address using the name of an employee.
 * Should probably be moved to a service to check if the address already exists
 */
@Embeddable
data class CompanyMail(val mail: String) {

    private companion object {
        val domain = "company.com"
        fun createMailFromName(firstname: String, lastname: String): String {
            return "${firstname.first().toLowerCase()}.${lastname.toLowerCase()}@$domain"
        }
    }

    constructor(firstname: String, lastname: String) : this(createMailFromName(firstname, lastname))

}