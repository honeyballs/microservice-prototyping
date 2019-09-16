package com.example.employeeadministration.model

import com.example.employeeadministration.model.events.EmployeeChangedJobPositionCompensation
import com.example.employeeadministration.model.events.EmployeeChangedNameCompensation
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

class EmployeeTests {

    var employee: Employee? = null
    val startSalary = BigDecimal(40.50565)
    val address = Address("Teststr.", 17, "Berlin", ZipCode(12345))
    val bankDetails = BankDetails("128319815719", "4712841", "Sparkasse")
    val position = Position(13L, "Consultant", BigDecimal(30.00), BigDecimal(50.00))
    val department = Department(14L, "Development")


    @Before
    fun setupEmployee() {
        employee = Employee(1L, "Max", "Mustermann", LocalDate.now().minusYears(26), address, bankDetails, department, position, startSalary, null)
    }

    @Test
    fun shouldRoundSalary() {
        Assertions.assertThat(employee!!.hourlyRate).isEqualTo(startSalary.setScale(2, RoundingMode.HALF_UP))
    }

    @Test
    fun employeeShouldMove() {
        employee!!.moveToNewAddress(Address("Andere Straße", 17, "Berlin", ZipCode(12345)))
        Assertions.assertThat(employee!!.address).isNotEqualTo(address)
    }

    @Test
    fun employeeShouldReceiveRaise() {
        val raise = BigDecimal(3.456)
        employee!!.receiveRaiseBy(raise)
        Assertions.assertThat(employee!!.hourlyRate).isEqualTo(startSalary.setScale(2, RoundingMode.HALF_UP).add(raise).setScale(2, RoundingMode.HALF_UP))
    }

    @Test
    fun employeeShouldSwitchBank() {
        val bankDetailsTest = BankDetails("123124124", "46346", "VR Bank")
        employee!!.switchBankDetails(bankDetailsTest)
        Assertions.assertThat(employee!!.bankDetails).isEqualTo(bankDetailsTest)
    }

    @Test
    fun employeeShouldBeMovedToNewPosition() {
        val positionTest = Position(31L,"Senior Consultant", BigDecimal(55.00), BigDecimal(70.90))
        employee!!.changeJobPosition(positionTest, null)
        Assertions.assertThat(employee!!.position).isEqualTo(positionTest)
        Assertions.assertThat(employee!!.hourlyRate).isEqualTo(positionTest.minHourlyWage.setScale(2, RoundingMode.HALF_UP))

        // Event checks
        Assertions.assertThat(employee!!.events().size).isEqualTo(1)
        val comp = employee!!.events()[0].compensatingAction as EmployeeChangedJobPositionCompensation
        Assertions.assertThat(comp.positionId).isEqualTo(position.id!!)
        Assertions.assertThat(comp.originalEventId).isEqualTo(employee!!.events()[0].id)

        employee!!.changeJobPosition(position, BigDecimal(35.00))
        Assertions.assertThat(employee!!.hourlyRate).isEqualTo(BigDecimal(35.00).setScale(2, RoundingMode.HALF_UP))
    }

    @Test
    fun mailShouldBeCorrect() {
        val mail = CompanyMail(employee!!.firstname, employee!!.lastname)
        Assertions.assertThat(employee!!.companyMail).isEqualTo(mail)
    }

    @Test
    fun nameChangeShouldChangeMail() {
        val lastname = "Schmidt"
        val companyMail = CompanyMail(employee!!.firstname, lastname)
        employee!!.changeName(null, lastname)
        Assertions.assertThat(employee!!.lastname).isEqualTo(lastname)
        Assertions.assertThat(employee!!.companyMail).isEqualTo(companyMail)

        // Event checks
        Assertions.assertThat(employee!!.events().size).isEqualTo(1)
        val comp = employee!!.events()[0].compensatingAction as EmployeeChangedNameCompensation
        Assertions.assertThat(comp.lastname).isEqualTo("Mustermann")
    }

}