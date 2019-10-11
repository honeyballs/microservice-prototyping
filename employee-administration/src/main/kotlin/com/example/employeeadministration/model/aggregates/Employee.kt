package com.example.employeeadministration.model.aggregates

import com.example.employeeadministration.model.valueobjects.Address
import com.example.employeeadministration.model.valueobjects.BankDetails
import com.example.employeeadministration.model.valueobjects.CompanyMail
import com.example.employeeadministration.model.dto.EmployeeKfk
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import javax.persistence.*

const val EMPLOYEE_AGGREGATE_NAME = "employee"

/**
 * Aggregate Employee encapsulating necessary Value Objects and handling possible invariants
 *
 */
@Entity
class Employee(@Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long?,
               var firstname: String,
               var lastname: String,
               val birthday: LocalDate,
               @Embedded var address: Address,
               @Embedded var bankDetails: BankDetails,
               @ManyToOne @JoinColumn(name = "fk_department") var department: Department,
               @ManyToOne @JoinColumn(name = "fk_position") var position: Position,
               hourlyRate: BigDecimal,
               companyMail: CompanyMail?,
               var deleted: Boolean = false) : EventAggregate<EmployeeKfk>() {

    // initialize it rounded. Apparently the custom setter is not applied to the initialization
    var hourlyRate: BigDecimal = hourlyRate.setScale(2, RoundingMode.HALF_UP)
        set(value) {
            // Always round the salary field
            field = value.setScale(2, RoundingMode.HALF_UP)
        }

    @Embedded
    var companyMail = companyMail ?: CompanyMail(firstname, lastname)

    init {
        aggregateName = EMPLOYEE_AGGREGATE_NAME
    }

    fun created() {
        if (id != null) {
            registerEvent(this.id!!, "created", null)
        }
    }

    fun moveToNewAddress(address: Address) {
        val from = mapAggregateToKafkaDto()
        this.address = address
        registerEvent(this.id!!, "updated", from)
    }

    fun receiveRaiseBy(raiseAmount: BigDecimal) {
        val from = mapAggregateToKafkaDto()
        this.hourlyRate = hourlyRate.add(raiseAmount)
        registerEvent(this.id!!, "updated", from)
    }

    fun switchBankDetails(bankDetails: BankDetails) {
        val from = mapAggregateToKafkaDto()
        this.bankDetails = bankDetails
        registerEvent(this.id!!, "updated", from)
    }

    /**
     * Changes the position of an employee
     *
     * @param newSalary If no salary is provided the mininum of the provided position is used
     */
    fun changeJobPosition(position: Position, newSalary: BigDecimal?) {
        val from = mapAggregateToKafkaDto()
        this.position = position
        this.hourlyRate = newSalary ?: position.minHourlyWage
        registerEvent(this.id!!, "updated", from)
    }

    fun moveToAnotherDepartment(department: Department) {
        val from = mapAggregateToKafkaDto()
        this.department = department
        registerEvent(this.id!!, "updated", from)
    }

    /**
     * Change the name(s) of a employee which subsequently changes the mail address
     */
    fun changeName(firstname: String?, lastname: String?) {
        val from = mapAggregateToKafkaDto()
        this.firstname = firstname ?: this.firstname
        this.lastname = lastname ?: this.lastname
        this.companyMail = CompanyMail(this.firstname, this.lastname)
        registerEvent(this.id!!, "updated", from)
    }

    fun deleteEmployee() {
        val from = mapAggregateToKafkaDto()
        deleted = true
        registerEvent(this.id!!, "deleted", from)
    }

    override fun mapAggregateToKafkaDto(): EmployeeKfk {
        return EmployeeKfk(this.id!!, this.firstname, this.lastname, this.birthday, this.address, this.bankDetails, this.department.id!!, this.position.id!!, this.hourlyRate, this.companyMail, this.deleted)
    }

    override fun toString(): String {
        return "$lastname, $firstname, born on: ${birthday.toString()} - Position: ${position.toString()}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Employee) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

}