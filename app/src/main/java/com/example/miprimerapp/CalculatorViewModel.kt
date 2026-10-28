package com.example.miprimerapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

private const val ERROR = "Error"
private const val MAX_DIGITS = 9

class CalculatorViewModel : ViewModel() {
    private val model = CalculatorModel()
    var result by mutableStateOf("0")
        private set
    var expression by mutableStateOf("")
        private set

    private var number1 = ""
    private var number2 = ""
    private var operator = ""

    private var finished = false
    private val digits = ('0'..'9').map { it.toString() }

    private var current: String
        get() = if (operator.isEmpty()) number1 else number2
        set(value) {
            if (operator.isEmpty()) number1 = value else number2 = value
            updateScreen()
        }

    fun manageClickButtons(button: String) {
        when (button) {
            in digits -> writeDigit(button)
            "," -> writeComma()
            "+", "-", "×", "÷" -> chooseOperator(button)
            "=" -> calculateResult()
            "AC" -> clearAll()
            "⌫" -> deleteLastDigit()
            "+/-" -> changeSign()
            "%" -> calculatePercent()
        }
    }

    private fun updateScreen() {
        result = when {
            operator.isNotEmpty() -> "$number1 $operator ${showSecond(number2)}".trim()
            number1.isEmpty() -> "0"
            else -> number1
        }
    }
    private fun showSecond(number: String): String {
        return if (number.startsWith("-")) "($number)" else number
    }

    private fun startNewIfFinished() {
        if (finished) {
            number1 = ""
            expression = ""
            finished = false
        }
    }

    private fun writeDigit(digit: String) {
        startNewIfFinished()
        current = current.removeSuffix("%")
        if (current.count { it.isDigit() } >= MAX_DIGITS) return

        current = when (current) {
            "0" -> digit
            "-0" -> "-$digit"
            else -> current + digit
        }
    }

    private fun writeComma() {
        startNewIfFinished()
        current = current.removeSuffix("%")

        if (current.isEmpty()) {
            current = "0,"
        } else if (',' !in current) {
            current += ","
        }
    }

    private fun chooseOperator(newOperator: String) {
        if (number1.isEmpty()) number1 = "0"
        number1 = number1.trimEnd(',')

        if (operator.isNotEmpty() && number2.isNotEmpty()) {
            val partial = calculate(number1, number2, operator)
            if (partial == ERROR) {
                showError()
                return
            }
            number1 = partial
            number2 = ""
        }

        operator = newOperator
        finished = false
        expression = ""
        updateScreen()
    }

    private fun calculateResult() {
        if (operator.isEmpty()) {
            if (number1.endsWith("%")) {
                val answer = calculate(number1, "1", "×")
                expression = "$number1 ="
                number1 = answer
                finished = true
                result = answer
            }
            return
        }

        if (number2.isEmpty()) number2 = number1
        number2 = number2.trimEnd(',')

        val answer = calculate(number1, number2, operator)
        if (answer == ERROR) {
            showError()
            return
        }

        expression = "$number1 $operator ${showSecond(number2)} ="
        number1 = answer
        number2 = ""
        operator = ""
        finished = true
        result = answer
    }

    private fun calculatePercent() {
        if (current.isEmpty() || current == "0") return

        if (current.endsWith("%")) {
            current = current.dropLast(1)
        } else {
            current = current + "%"
        }
    }
    private fun changeSign() {
        current = when {
            operator.isNotEmpty() && number2.isEmpty() -> "-0"
            current.isEmpty() || current == "0" -> return
            current.startsWith("-") -> current.drop(1)
            else -> "-$current"
        }
    }

    private fun deleteLastDigit() {
        if (current.isEmpty()) return

        if (finished) {
            finished = false
            expression = ""
        }

        val newValue = current.dropLast(1)
        current = if (newValue.isEmpty() || newValue == "-") "0" else newValue
    }

    private fun clearAll() {
        number1 = ""
        number2 = ""
        operator = ""
        finished = false
        result = "0"
        expression = ""
    }

    private fun showError() {
        clearAll()
        result = ERROR
    }

    private fun toNumber(text: String): Double? {
        if (text.endsWith("%")) {
            val number = text.dropLast(1).replace(",", ".").toDoubleOrNull()
            return if (number == null) null else number / 100
        }
        return text.replace(",", ".").toDoubleOrNull()
    }


    private fun calculate(n1: String, n2: String, op: String): String {
        val a = toNumber(n1) ?: return ERROR
        val b = toNumber(n2) ?: return ERROR

        val answer = when (op) {
            "+" -> model.add(a, b)
            "-" -> model.subtraction(a, b)
            "×" -> model.multiply(a, b)
            "÷" -> if (b != 0.0) model.divide(a, b) else return ERROR
            else -> return ERROR
        }

        return formatNumber(answer)
    }


    private fun formatNumber(number: Double): String {
        return if (number % 1 == 0.0) {
            number.toLong().toString()
        } else {
            number.toString().replace(".", ",")
        }
    }
}