package com.example.employeeadministration.model

import com.example.employeeadministration.model.valueobjects.CompanyMail
import com.example.employeeadministration.model.valueobjects.ZipCode
import org.assertj.core.api.Assertions
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.util.*

class VOTests {

    // A thrown Exception will be inserted here
    @Rule
    @JvmField
    var expectedZipException: ExpectedException = ExpectedException.none()

    @Test
    @Throws(Exception::class)
    fun zipCodeShouldThrowException() {
        val correctZipCode = ZipCode(12345)
        Assertions.assertThat(correctZipCode.zip).isEqualTo(12345)
        // Set up the exception test and run code to throw it
        expectedZipException.expect(Exception::class.java)
        expectedZipException.expectMessage("The zip code provided does not match the required length of ${ZipCode.ALLOWED_LENGTHS_PER_COUNTRY[Locale.GERMANY]} digits.")
        val exceptionZip = ZipCode(123456)
    }

    @Test
    fun equalsShouldOnlyCompareProperties() {
        val zipCode = ZipCode(12345)
        val zipCodeToCompare = ZipCode(12345)
        // Same property, different instances, should be equal
        Assertions.assertThat(zipCode == zipCodeToCompare).isTrue()
    }

    @Test
    fun shouldCreateTheCorrectMailAddress() {
        val mail = CompanyMail("m.mustermann@company.com")
        val createdMail = CompanyMail("Max", "Mustermann")
        Assertions.assertThat(mail).isEqualTo(createdMail)
    }

}