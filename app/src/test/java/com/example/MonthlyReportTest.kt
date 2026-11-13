package com.example

import com.example.data.model.AttendanceEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.SalarySettings
import com.example.domain.DateUtils
import com.example.domain.MonthlyReport
import org.junit.Assert.assertEquals
import org.junit.Test

class MonthlyReportTest {

    @Test
    fun calculate_matchesExpectedEarningsAndBalance() {
        val settings = SalarySettings(dailyWage = 600.0, otRate = 100.0)
        val attendance = listOf(
            AttendanceEntity("2026-11-01", "Present", dutyHours = 8.0, otHours = 2.0),
            AttendanceEntity("2026-11-02", "Half Day", dutyHours = 4.0, otHours = 1.0),
            AttendanceEntity("2026-11-03", "Absent", dutyHours = 0.0, otHours = 0.0),
            AttendanceEntity("2026-11-04", "Paid Leave", dutyHours = 8.0, otHours = 0.0),
            AttendanceEntity("2026-11-05", "Weekly Off", dutyHours = 0.0, otHours = 0.0)
        )
        val payments = listOf(
            PaymentEntity(id = 1, amount = 1000.0, dateIso = "2026-11-04", note = "Advance")
        )

        val report = MonthlyReport.calculate(attendance, payments, settings)

        // Present (600 + 200) + Half Day (300 + 100) + Absent (0) + Paid Leave (600) + Weekly Off (0) = 1800
        assertEquals(1, report.presentCount)
        assertEquals(1, report.halfDayCount)
        assertEquals(1, report.absentCount)
        assertEquals(1, report.paidLeaveCount)
        assertEquals(1, report.weeklyOffCount)
        assertEquals(20.0, report.totalWorkHours, 0.001)
        assertEquals(3.0, report.totalOtHours, 0.001)
        assertEquals(1800.0, report.totalEarned, 0.001)
        assertEquals(1000.0, report.totalPaid, 0.001)
        assertEquals(800.0, report.balanceDue, 0.001)
    }

    @Test
    fun dateUtils_shiftMonthAndFormatting() {
        assertEquals("2026-12", DateUtils.shiftMonth("2026-11", 1))
        assertEquals("2026-10", DateUtils.shiftMonth("2026-11", -1))
        assertEquals("₹1,800", DateUtils.formatRupees(1800.0))
        assertEquals("8", DateUtils.formatHours(8.0))
        assertEquals("8.5", DateUtils.formatHours(8.5))
    }
}
