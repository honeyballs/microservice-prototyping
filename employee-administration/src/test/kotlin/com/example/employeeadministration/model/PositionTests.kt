package com.example.employeeadministration.model

import org.assertj.core.api.Assertions
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class PositionTests {

    @Test
    fun shouldRoundValuesCorrectly() {
        val min = BigDecimal(30.555)
        val max = BigDecimal(50.4321)
        val pos = Position(1L, "Test Pos", min, max)
        Assertions.assertThat(pos.minHourlyWage).isEqualTo(min.setScale(2, RoundingMode.HALF_UP))
        Assertions.assertThat(pos.maxHourlyWage).isEqualTo(max.setScale(2, RoundingMode.HALF_UP))
    }

    @Test
    fun shouldCorrectlyCalculateWhetherSalaryIsInRange() {
        val position = Position(1L, "Test Pos", BigDecimal(30.20), BigDecimal(40.00))
        val inRange = BigDecimal(35.72)
        val outOfRange = BigDecimal(40.01)
        Assertions.assertThat(position.isRateInRange(inRange)).isTrue()
        Assertions.assertThat(position.isRateInRange(outOfRange)).isFalse()
    }

}