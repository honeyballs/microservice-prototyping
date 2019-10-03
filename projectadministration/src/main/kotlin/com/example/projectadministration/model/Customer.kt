package com.example.projectadministration.model

import java.lang.Exception
import java.util.*
import javax.persistence.*

@Entity
data class Customer(@Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long?, var customerName: String, @Embedded var address: Address, @Embedded var contact: CustomerContact, var deleted: Boolean = false) {

    fun changeCustomerContact(contact: CustomerContact) {
        this.contact = contact
    }

    fun moveCompanyLocation(address: Address) {
        this.address = address
    }

    fun changeName(name: String) {
        this.customerName = name
    }

    fun deleteCustomer() {
        this.deleted = true
    }

}

@Embeddable
data class CustomerContact(val firstname: String, val lastname: String, val mail: String, val phone: String)


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