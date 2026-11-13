package com.example.domain

import com.example.data.model.AttendanceEntity
import com.example.data.model.AttendanceStatus
import com.example.data.model.PaymentEntity
import com.example.data.model.SalarySettings

data class MonthlyReport(
    val presentCount: Int = 0,
    val halfDayCount: Int = 0,
    val absentCount: Int = 0,
    val paidLeaveCount: Int = 0,
    val weeklyOffCount: Int = 0,
    val totalWorkHours: Double = 0.0,
    val totalOtHours: Double = 0.0,
    val totalEarned: Double = 0.0,
    val totalPaid: Double = 0.0,
    val balanceDue: Double = 0.0
) {
    val totalMarkedDays: Int
        get() = presentCount + halfDayCount + absentCount + paidLeaveCount + weeklyOffCount

    val payableDaysEquivalent: Double
        get() = presentCount + (halfDayCount * 0.5) + paidLeaveCount

    companion object {
        fun calculate(
            attendance: List<AttendanceEntity>,
            payments: List<PaymentEntity>,
            settings: SalarySettings
        ): MonthlyReport {
            var present = 0
            var half = 0
            var absent = 0
            var leave = 0
            var off = 0
            var workHours = 0.0
            var otHours = 0.0
            var earned = 0.0

            for (record in attendance) {
                val st = record.statusEnum()
                when (st) {
                    AttendanceStatus.PRESENT -> present++
                    AttendanceStatus.HALF_DAY -> half++
                    AttendanceStatus.ABSENT -> absent++
                    AttendanceStatus.PAID_LEAVE -> leave++
                    AttendanceStatus.WEEKLY_OFF -> off++
                }
                workHours += record.dutyHours
                otHours += record.otHours
                earned += (st.dayWeight * settings.dailyWage) + (record.otHours * settings.otRate)
            }

            val paid = payments.sumOf { it.amount }
            return MonthlyReport(
                presentCount = present,
                halfDayCount = half,
                absentCount = absent,
                paidLeaveCount = leave,
                weeklyOffCount = off,
                totalWorkHours = workHours,
                totalOtHours = otHours,
                totalEarned = earned,
                totalPaid = paid,
                balanceDue = earned - paid
            )
        }

        fun calculateDayEarning(record: AttendanceEntity, settings: SalarySettings): Double {
            val st = record.statusEnum()
            return (st.dayWeight * settings.dailyWage) + (record.otHours * settings.otRate)
        }
    }
}
