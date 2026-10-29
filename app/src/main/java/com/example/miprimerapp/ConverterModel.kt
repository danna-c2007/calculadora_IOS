package com.example.miprimerapp

// Modelo que contiene la lógica matemática para la conversión de unidades de masa
class ConverterModel {

    // Convierte cualquier unidad de masa a gramos como unidad base interna
    private fun toGrams(value: Double, unit: String): Double {
        return when (unit) {
            "mg" -> value / 1000.0
            "g" -> value
            "kg" -> value * 1000.0
            "t" -> value * 1_000_000.0
            else -> value
        }
    }

    // Convierte desde gramos a la unidad de destino solicitada
    fun convert(value: Double, fromUnit: String, toUnit: String): Double {
        val grams = toGrams(value, fromUnit)
        return when (toUnit) {
            "mg" -> grams * 1000.0
            "g" -> grams
            "kg" -> grams / 1000.0
            "t" -> grams / 1_000_000.0
            else -> grams
        }
    }
}
