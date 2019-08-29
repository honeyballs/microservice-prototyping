package com.example.employeeadministration.model

import org.assertj.core.api.Assertions
import org.junit.Rule
import org.junit.Test
import org.junit.internal.runners.statements.ExpectException
import org.junit.rules.ExpectedException
import java.util.*

class VOTests {

    // A thrown Exception will be inserted here
    @Rule
    @JvmField
    var expectedZipException: ExpectedException = ExpectedException.none()

    @Test
    @Throws(Exception::class)
    fun testZipError() {
        val correctZipCode = ZipCode(12345)
        Assertions.assertThat(correctZipCode.zip).isEqualTo(12345)
        // Set up the exception test and run code to throw it
        expectedZipException.expect(Exception::class.java)
        expectedZipException.expectMessage("The zip code provided does not match the required length of ${ZipCode.ALLOWED_LENGTHS_PER_COUNTRY[Locale.GERMANY]} digits.")
        val exceptionZip = ZipCode(123456)
    }

}