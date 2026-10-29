package com.example.miprimerapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

private const val ERROR = "Error"
private const val MAX_DIGITS = 9

// ViewModel principal de la calculadora que maneja el estado, las entradas y la lógica de presentación
class CalculatorViewModel : ViewModel() {
    private val model = CalculatorModel()

    // Resultado mostrado en la pantalla principal
    var result by mutableStateOf("0")
        private set

    // Expresión matemática en curso
    var expression by mutableStateOf("")
        private set

    // Lista observable para almacenar el historial de operaciones realizadas
    val historyList = mutableStateListOf<String>()

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

    // Gestiona los clics de cada botón presionado en la interfaz de la calculadora
    fun manageClickButtons(button: String) {
        when (button) {
            in digits -> writeDigit(button)
            "," -> writeComma()
            "+", "-", "×", "÷" -> chooseOperator(button)
            "%" -> applyPercentage()
            "=" -> calculateResult()
            "AC" -> clearAll()
            "⌫" -> deleteLastDigit()
            "+/-" -> changeSign()
        }
    }

    // Actualiza la pantalla según los números y operador actuales
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

    // Escribe un dígito numérico respetando el límite máximo
    private fun writeDigit(digit: String) {
        startNewIfFinished()
        if (current.count { it.isDigit() } >= MAX_DIGITS) return

        current = when (current) {
            "0" -> digit
            "-0" -> "-$digit"
            else -> current + digit
        }
    }

    // Inserta la coma decimal
    private fun writeComma() {
        startNewIfFinished()

        if (current.isEmpty()) {
            current = "0,"
        } else if (',' !in current) {
            current += ","
        }
    }

    // Selecciona un operador aritmético
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

    // Calcula el resultado final y lo registra en el historial
    private fun calculateResult() {
        if (operator.isEmpty()) return

        if (number2.isEmpty()) number2 = number1
        number2 = number2.trimEnd(',')

        val answer = calculate(number1, number2, operator)
        if (answer == ERROR) {
            showError()
            return
        }

        val fullExpression = "$number1 $operator ${showSecond(number2)}"
        expression = "$fullExpression ="
        val record = "$fullExpression = $answer"
        historyList.add(0, record) // Guarda la operación en el historial

        number1 = answer
        number2 = ""
        operator = ""
        finished = true
        result = answer
    }

    // Cambia el signo del número actual (+/-)
    private fun changeSign() {
        if ((current.isEmpty()) || (current == "0")) return
        current = when {
            operator.isNotEmpty() && number2.isEmpty() -> "-0"
            current.startsWith("-") -> current.drop(1)
            else -> "-$current"
        }
    }

    // Aplica la función de porcentaje sobre el número actual
    private fun applyPercentage() {
        if (finished) {
            finished = false
        }

        if (operator.isEmpty()) {
            if (number1.isEmpty()) return
            val val1 = toNumber(number1) ?: return
            val percentVal = model.percentage(val1)
            number1 = formatNumber(percentVal)
            finished = true
        } else {
            if (number2.isEmpty()) {
                number2 = number1
            }
            val val1 = toNumber(number1) ?: return
            val val2 = toNumber(number2) ?: return

            val percentVal = when (operator) {
                "+", "-" -> val1 * model.percentage(val2)
                "×", "÷" -> model.percentage(val2)
                else -> model.percentage(val2)
            }
            number2 = formatNumber(percentVal)
        }
        updateScreen()
    }

    // Borra el último dígito ingresado (Backspace)
    private fun deleteLastDigit() {
        if (current.isEmpty()) return

        if (finished) {
            finished = false
            expression = ""
        }

        val newValue = current.dropLast(1)
        current = if ((newValue.isEmpty()) || (newValue == "-")) "0" else newValue
    }

    // Limpia todos los campos (AC)
    private fun clearAll() {
        number1 = ""
        number2 = ""
        operator = ""
        finished = false
        result = "0"
        expression = ""
    }

    // Muestra el estado de error en pantalla
    private fun showError() {
        clearAll()
        result = ERROR
    }

    // Convierte texto de la calculadora a Double para las operaciones
    private fun toNumber(text: String): Double? {
        return text.replace(",", ".").toDoubleOrNull()
    }

    // Realiza el cálculo llamando al modelo
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

    // Formatea el resultado numérico para visualización evitando imprecisiones de coma flotante
    private fun formatNumber(number: Double): String {
        if (number.isNaN() || number.isInfinite()) return ERROR
        val bd = java.math.BigDecimal.valueOf(number)
            .setScale(8, java.math.RoundingMode.HALF_UP)
            .stripTrailingZeros()
        return bd.toPlainString().replace(".", ",")
    }
}