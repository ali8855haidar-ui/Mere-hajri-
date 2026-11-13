package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.SalarySettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "salary_settings")

class SettingsDataStore(private val context: Context) {
    private val keyDailyWage = doublePreferencesKey("daily_wage")
    private val keyOtRate = doublePreferencesKey("ot_rate")

    val settingsFlow: Flow<SalarySettings> = context.settingsDataStore.data.map { prefs ->
        SalarySettings(
            dailyWage = prefs[keyDailyWage] ?: 600.0,
            otRate = prefs[keyOtRate] ?: 100.0
        )
    }

    suspend fun saveSettings(dailyWage: Double, otRate: Double) {
        context.settingsDataStore.edit { prefs ->
            prefs[keyDailyWage] = dailyWage
            prefs[keyOtRate] = otRate
        }
    }
}
