package com.example.projectadministration.model

import com.example.projectadministration.model.events.*
import java.lang.Exception
import java.util.*
import javax.persistence.*


const val CUSTOMER_TOPIC_NAME = "customer"

@Entity
data class Customer(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long?,
        var customerName: String,
        @Embedded var address: Address,
        @Embedded var contact: CustomerContact,
        var deleted: Boolean = false
): EventAggregate<CustomerKfk>() {

    init {
        TOPIC_NAME = CUSTOMER_TOPIC_NAME
    }

    fun created() {
        if (id != null) {
            registerEvent(this.id!!, CustomerEvent(mapAggregateToKafkaDto(), CustomerCompensation(mapAggregateToKafkaDto(), EventType.CREATE), EventType.CREATE))
        }
    }

    fun changeCustomerContact(contact: CustomerContact) {
        val comp = CustomerCompensation(mapAggregateToKafkaDto(), EventType.UPDATE)
        this.contact = contact
        registerEvent(this.id!!, CustomerEvent(mapAggregateToKafkaDto(), comp, EventType.UPDATE))
    }

    fun moveCompanyLocation(address: Address) {
        val comp = CustomerCompensation(mapAggregateToKafkaDto(), EventType.UPDATE)
        this.address = address
        registerEvent(this.id!!, CustomerEvent(mapAggregateToKafkaDto(), comp, EventType.UPDATE))
    }

    fun changeName(name: String) {
        val comp = CustomerCompensation(mapAggregateToKafkaDto(), EventType.UPDATE)
        this.customerName = name
        registerEvent(this.id!!, CustomerEvent(mapAggregateToKafkaDto(), comp, EventType.UPDATE))
    }

    fun deleteCustomer() {
        val comp = CustomerCompensation(mapAggregateToKafkaDto(), EventType.DELETE)
        this.deleted = true
        registerEvent(this.id!!, CustomerEvent(mapAggregateToKafkaDto(), comp, EventType.DELETE))
    }

    override fun mapAggregateToKafkaDto(): CustomerKfk {
        return CustomerKfk(this.id!!, this.customerName, this.address, this.contact, this.deleted)
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