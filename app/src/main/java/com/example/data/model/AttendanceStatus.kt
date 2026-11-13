package com.example.data.model

enum class AttendanceStatus(
    val storageKey: String,
    val labelHindi: String,
    val shortLabel: String,
    val dayWeight: Double,
    val defaultDutyHours: Double
) {
    PRESENT(
        storageKey = "Present",
        labelHindi = "Present",
        shortLabel = "Present",
        dayWeight = 1.0,
        defaultDutyHours = 8.0
    ),
    HALF_DAY(
        storageKey = "Half Day",
        labelHindi = "Half Day",
        shortLabel = "Half Day",
        dayWeight = 0.5,
        defaultDutyHours = 4.0
    ),
    ABSENT(
        storageKey = "Absent",
        labelHindi = "Absent",
        shortLabel = "Absent",
        dayWeight = 0.0,
        defaultDutyHours = 0.0
    ),
    PAID_LEAVE(
        storageKey = "Paid Leave",
        labelHindi = "Paid Leave",
        shortLabel = "Paid Leave",
        dayWeight = 1.0,
        defaultDutyHours = 8.0
    ),
    WEEKLY_OFF(
        storageKey = "Weekly Off",
        labelHindi = "Weekly Off",
        shortLabel = "Weekly Off",
        dayWeight = 0.0,
        defaultDutyHours = 0.0
    );

    companion object {
        fun fromStorageKey(key: String): AttendanceStatus {
            return entries.firstOrNull { it.storageKey.equals(key, ignoreCase = true) } ?: PRESENT
        }
    }
}
