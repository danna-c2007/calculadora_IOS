package com.example.miprimerapp

// Modelo que contiene las operaciones aritméticas básicas de la calculadora
class CalculatorModel {

    // Suma dos números
    fun add(num1: Double, num2: Double): Double {
        return num1 + num2
    }

    // Resta dos números
    fun subtraction(num1: Double, num2: Double): Double {
        return num1 - num2
    }

    // Multiplica dos números
    fun multiply(num1: Double, num2: Double): Double {
        return num1 * num2
    }

    // Divide dos números
    fun divide(num1: Double, num2: Double): Double {
        return num1 / num2
    }

    // Calcula el módulo (residuo) entre dos números
    fun module(num1: Double, num2: Double): Double {
        return num1 % num2
    }
}
