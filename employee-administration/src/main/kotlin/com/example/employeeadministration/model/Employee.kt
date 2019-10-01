package com.example.employeeadministration.model

import com.example.employeeadministration.model.events.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import javax.persistence.*

const val EMPLOYEE_TOPIC_NAME = "employee"

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
               var deleted: Boolean = false) : EventAggregate() {

    // initialize it rounded. Apparently the custom setter is not applied to the initialization
    var hourlyRate: BigDecimal = hourlyRate.setScale(2, RoundingMode.HALF_UP)
        set(value) {
            // Always round the salary field
            field = value.setScale(2, RoundingMode.HALF_UP)
        }

    @Embedded
    var companyMail = companyMail ?: CompanyMail(firstname, lastname)

    init {
        TOPIC_NAME = EMPLOYEE_TOPIC_NAME
    }

    fun created() {
        if (id != null) {
            registerEvent(this.id!!, EmployeeEvent(this, EmployeeCompensation(this, EventType.CREATE), EventType.CREATE))
        }
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
     * Changes the position of an employee
     *
     * @param newSalary If no salary is provided the mininum of the provided position is used
     */
    fun changeJobPosition(position: Position, newSalary: BigDecimal?) {
        val compensation = EmployeeCompensation(this.copy(), EventType.UPDATE)
        this.position = position
        this.hourlyRate = newSalary ?: position.minHourlyWage
        registerEvent(id !!, EmployeeEvent(this, compensation, EventType.UPDATE))
    }

    fun moveToAnotherDepartment(department: Department) {
        val compensation = EmployeeCompensation(this.copy(), EventType.UPDATE)
        this.department = department
        registerEvent(id !!, EmployeeEvent(this, compensation, EventType.UPDATE))
    }

    /**
     * Change the name(s) of a employee which subsequently changes the mail address
     */
    fun changeName(firstname: String?, lastname: String?) {
        val compensation = EmployeeCompensation(this.copy(), EventType.UPDATE)
        this.firstname = firstname ?: this.firstname
        this.lastname = lastname ?: this.lastname
        this.companyMail = CompanyMail(this.firstname, this.lastname)
        registerEvent(id !!, EmployeeEvent(this, compensation, EventType.UPDATE))
    }

    fun deleteEmployee() {
        deleted = true
        registerEvent(this.id!!, EmployeeEvent(this, EmployeeCompensation(this, EventType.DELETE), EventType.DELETE))
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

    fun copy(): Employee {
        return Employee(
                this.id,
                this.firstname,
                this.lastname,
                this.birthday,
                this.address.copy(),
                this.bankDetails.copy(),
                this.department.copy(),
                this.position.copy(),
                this.hourlyRate,
                this.companyMail.copy(),
                this.deleted)
    }


}