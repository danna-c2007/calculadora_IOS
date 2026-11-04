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

    // Números y operadores ya confirmados, siempre terminan en operador.
    // Ejemplo: al escribir 9 + 2 ÷ queda ["9", "+", "2", "÷"]
    private val tokens = mutableListOf<String>()

    // Número que se está escribiendo en este momento
    private var current = ""
        set(value) {
            field = value
            updateScreen()
        }

    private var finished = false
    private val digits = ('0'..'9').map { it.toString() }

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

    // Los números negativos (salvo el primero) se muestran entre paréntesis.
    // Los números están en posiciones pares y los operadores en las impares.
    private fun format(token: String, index: Int): String {
        return if (index > 0 && index % 2 == 0 && token.startsWith("-")) "($token)" else token
    }

    // Actualiza la pantalla con todo lo que se ha escrito hasta ahora
    private fun updateScreen() {
        val parts = tokens.mapIndexed { i, t -> format(t, i) }.toMutableList()
        if (current.isNotEmpty()) parts.add(format(current, tokens.size))
        result = if (parts.isEmpty()) "0" else parts.joinToString(" ")
    }

    private fun startNewIfFinished() {
        if (finished) {
            tokens.clear()
            current = ""
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

    // Selecciona un operador: NO calcula nada, solo guarda el número y el operador
    private fun chooseOperator(newOperator: String) {
        if (current.isEmpty()) {
            if (tokens.isEmpty()) {
                current = "0"
            } else {
                // Ya había un operador sin número después: se reemplaza por el nuevo
                tokens[tokens.lastIndex] = newOperator
                updateScreen()
                return
            }
        }

        tokens.add(current.trimEnd(','))
        tokens.add(newOperator)
        finished = false
        expression = ""
        current = "" // también actualiza la pantalla
    }

    // Calcula toda la expresión de una vez y la registra en el historial
    private fun calculateResult() {
        if (tokens.isEmpty()) return

        // Si no se escribió el último número, se repite el anterior (ej: 5 + = da 10)
        val last = current.ifEmpty { tokens[tokens.size - 2] }.trimEnd(',')
        val all = tokens + last

        val answer = evaluate(all)
        if (answer == ERROR) {
            showError()
            return
        }

        val fullExpression = all.mapIndexed { i, t -> format(t, i) }.joinToString(" ")
        expression = "$fullExpression ="
        historyList.add(0, "$fullExpression = $answer") // Guarda la operación en el historial

        tokens.clear()
        finished = true
        current = answer
    }

    // Evalúa respetando la jerarquía: primero × y ÷, después + y -
    // Ejemplo: 9 + 2 ÷ 2 = 10
    private fun evaluate(items: List<String>): String {
        val values = mutableListOf<Double>()
        val ops = mutableListOf<String>()

        values.add(toNumber(items[0]) ?: return ERROR)

        var i = 1
        while (i < items.size) {
            val op = items[i]
            val next = toNumber(items[i + 1]) ?: return ERROR

            if (op == "×" || op == "÷") {
                // Se resuelve de inmediato con el número anterior
                val prev = values.removeAt(values.lastIndex)
                val partial = if (op == "×") {
                    model.multiply(prev, next)
                } else {
                    if (next == 0.0) return ERROR
                    model.divide(prev, next)
                }
                values.add(partial)
            } else {
                // + y - se dejan pendientes para el final
                ops.add(op)
                values.add(next)
            }
            i += 2
        }

        var total = values[0]
        for (j in ops.indices) {
            val b = values[j + 1]
            total = if (ops[j] == "+") model.add(total, b) else model.subtraction(total, b)
        }

        return formatNumber(total)
    }

    // Cambia el signo del número actual (+/-)
    private fun changeSign() {
        if (current == "0") return
        if (current.isEmpty() && tokens.isEmpty()) return

        current = when {
            current.isEmpty() -> "-0"
            current.startsWith("-") -> current.drop(1)
            else -> "-$current"
        }
    }

    // Aplica la función de porcentaje sobre el número actual
    private fun applyPercentage() {
        finished = false

        if (tokens.isEmpty()) {
            if (current.isEmpty()) return
            val value = toNumber(current) ?: return
            current = formatNumber(model.percentage(value))
            finished = true
        } else {
            val operator = tokens.last()
            val value = toNumber(current.ifEmpty { tokens[tokens.size - 2] }) ?: return

            val percentVal = when (operator) {
                // En + y - el porcentaje es sobre lo acumulado antes del operador
                "+", "-" -> {
                    val base = toNumber(evaluate(tokens.dropLast(1))) ?: return
                    base * model.percentage(value)
                }
                else -> model.percentage(value)
            }
            current = formatNumber(percentVal)
        }
    }

    // Borra el último dígito ingresado (Backspace)
    private fun deleteLastDigit() {
        if (current.isEmpty()) {
            // Si acaba de escribir un operador, lo quita y recupera el número anterior
            if (tokens.size >= 2) {
                tokens.removeAt(tokens.lastIndex)
                current = tokens.removeAt(tokens.lastIndex)
            }
            return
        }

        if (finished) {
            finished = false
            expression = ""
        }

        // Si no queda nada (o solo el signo "-"), el número queda vacío.
        // Así el siguiente ⌫ puede borrar el operador y seguir hacia atrás.
        val newValue = current.dropLast(1)
        current = if (newValue == "-") "" else newValue
    }

    // Limpia todos los campos (AC)
    private fun clearAll() {
        tokens.clear()
        finished = false
        expression = ""
        current = ""
        result = "0"
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

    // Formatea el resultado numérico para visualización evitando imprecisiones de coma flotante
    private fun formatNumber(number: Double): String {
        if (number.isNaN() || number.isInfinite()) return ERROR
        val bd = java.math.BigDecimal.valueOf(number)
            .setScale(8, java.math.RoundingMode.HALF_UP)
            .stripTrailingZeros()
        return bd.toPlainString().replace(".", ",")
    }
}

