package com.example.employeeadministration.model

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
    val position = Position("Consultant", BigDecimal(30.00), BigDecimal(50.00))
    val department = Department("Development")
    val jobDetails = JobDetails(12L, department, position)


    @Before
    fun setupEmployee() {
        employee = Employee(1L, "Max", "Mustermann", LocalDate.now().minusYears(26), address, bankDetails, jobDetails, startSalary, null)
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
        val positionTest = Position("Senior Consultant", BigDecimal(55.00), BigDecimal(70.90))
        val newJobDetails = JobDetails(13L, department, positionTest)
        employee!!.changeJobPosition(newJobDetails, null)
        Assertions.assertThat(employee!!.jobDetails.position).isEqualTo(positionTest)
        Assertions.assertThat(employee!!.hourlyRate).isEqualTo(positionTest.minHourlyWage.setScale(2, RoundingMode.HALF_UP))
        employee!!.changeJobPosition(jobDetails, BigDecimal(35.00))
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
    }

}