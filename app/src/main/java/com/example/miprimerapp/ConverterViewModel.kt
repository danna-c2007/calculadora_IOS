package com.example.miprimerapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// ViewModel que gestiona el estado y la lógica de negocio para la pantalla de conversión de masa
class ConverterViewModel : ViewModel() {
    private val model = ConverterModel()

    // Número ingresado por el usuario a convertir
    var inputNumber by mutableStateOf("")
        private set

    // Unidad de masa de origen (mg, g, kg, t)
    var selectedFromUnit by mutableStateOf("g")
        private set

    // Unidad de masa de destino (mg, g, kg, t)
    var selectedToUnit by mutableStateOf("kg")
        private set

    // Resultado de la conversión
    var outputResult by mutableStateOf("0")
        private set

    // Actualiza el valor de entrada y recalcula la conversión
    fun onInputChanged(newInput: String) {
        inputNumber = newInput
        calculateConversion()
    }

    // Establece la unidad de origen y recalcula
    fun updateFromUnit(unit: String) {
        selectedFromUnit = unit
        calculateConversion()
    }

    // Establece la unidad de destino y recalcula
    fun updateToUnit(unit: String) {
        selectedToUnit = unit
        calculateConversion()
    }

    // Realiza el cálculo utilizando el modelo de conversión
    private fun calculateConversion() {
        val value = inputNumber.replace(",", ".").toDoubleOrNull() ?: 0.0
        val result = model.convert(value, selectedFromUnit, selectedToUnit)
        outputResult = formatNumber(result)
    }

    // Formatea el número resultante para evitar decimales innecesarios
    private fun formatNumber(number: Double): String {
        return if (number % 1 == 0.0 && number < 1e12 && number > -1e12) {
            number.toLong().toString()
        } else {
            number.toString().replace(".", ",")
        }
    }
}