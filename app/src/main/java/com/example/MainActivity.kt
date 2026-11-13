package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.MeriHajiriDatabase
import com.example.data.local.SettingsDataStore
import com.example.data.repository.HajiriRepository
import com.example.ui.HajiriViewModel
import com.example.ui.MeriHajiriApp
import com.example.ui.theme.MeriHajiriTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = MeriHajiriDatabase.getInstance(applicationContext)
        val settingsDataStore = SettingsDataStore(applicationContext)
        val repository = HajiriRepository(
            attendanceDao = database.attendanceDao(),
            paymentDao = database.paymentDao(),
            settingsDataStore = settingsDataStore
        )

        setContent {
            MeriHajiriTheme {
                val viewModel: HajiriViewModel = viewModel(
                    factory = HajiriViewModel.Factory(repository)
                )
                MeriHajiriApp(viewModel = viewModel)
            }
        }
    }
}
