package com.example.miprimerapp

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Configuración de la navegación entre pantallas (calculadora, historial y convertidor)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // ViewModel compartido para la calculadora y el historial
    val calculatorViewModel: CalculatorViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "calculator"
    ) {
        // Pantalla principal de la calculadora
        composable("calculator") {
            ShowCalculator(navController = navController, viewModel = calculatorViewModel)
        }
        // Pantalla de historial de operaciones
        composable("history") {
            ShowHistory(navController = navController, historyList = calculatorViewModel.historyList)
        }
        // Pantalla del convertidor de masa (miligramos, gramos, kilogramos, toneladas)
        composable("converter") {
            ShowConverter(navController = navController)
        }
    }
}
