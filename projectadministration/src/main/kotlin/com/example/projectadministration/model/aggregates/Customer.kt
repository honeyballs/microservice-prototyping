package com.example.projectadministration.model.aggregates

import com.example.projectadministration.model.dto.CustomerKfk
import java.lang.Exception
import java.util.*
import javax.persistence.*


const val CUSTOMER_AGGREGATE_NAME = "customer"

@Entity
class Customer(
        id: Long?,
        var customerName: String,
        @Embedded var address: Address,
        @Embedded var contact: CustomerContact,
        deleted: Boolean = false,
        override var aggregateName: String = CUSTOMER_AGGREGATE_NAME
): EventAggregate(id, deleted) {


    fun created() {
        if (id != null) {
            registerEvent(this.id!!, "created", null)
        }
    }

    fun changeCustomerContact(contact: CustomerContact) {
        val from = mapAggregateToKafkaDto()
        this.contact = contact
        registerEvent(this.id!!, "updated", from)
    }

    fun moveCompanyLocation(address: Address) {
        val from = mapAggregateToKafkaDto()
        this.address = address
        registerEvent(this.id!!, "updated", from)
    }

    fun changeName(name: String) {
        val from = mapAggregateToKafkaDto()
        this.customerName = name
        registerEvent(this.id!!, "updated", from)
    }

    override fun deleteAggregate() {
        val from = mapAggregateToKafkaDto()
        this.deleted = true
        registerEvent(this.id!!, "deleted", from)    }

    override fun mapAggregateToKafkaDto(): CustomerKfk {
        return CustomerKfk(this.id!!, this.customerName, this.address, this.contact, this.deleted, this.state)
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