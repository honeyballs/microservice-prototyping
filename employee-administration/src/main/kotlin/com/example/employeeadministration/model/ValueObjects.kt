package com.example.employeeadministration.model

import java.lang.Exception
import java.math.BigDecimal
import java.util.*

/**
 * Wrapper class for ZipCode which checks if the provided number matches the required length.
 * Not sure if necessary for DDD or if validation suffices.
 *
 */
class ZipCode(val zip: Int) {

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
data class Address(val street: String, val no: Int, val city: String, val zipCode: ZipCode)

/**
 * Value Object representing bank details.
 */
data class BankDetails(val iban: String, val bic: String, val bankName: String)

/**
 * Value Object representing the position an employee has in the company.
 * Should this be a VO? hourly wage could be mutable.
 */
data class Position(val title: String, val baseHourlyWageRange: ClosedRange<BigDecimal>)