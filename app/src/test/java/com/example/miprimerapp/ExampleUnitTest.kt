package com.example.miprimerapp

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun percentage_twentyFourPercentOf530() {
        val viewModel = CalculatorViewModel()
        // 530 × 24 % =
        "530".forEach { viewModel.manageClickButtons(it.toString()) }
        viewModel.manageClickButtons("×")
        "24".forEach { viewModel.manageClickButtons(it.toString()) }
        viewModel.manageClickButtons("%")
        viewModel.manageClickButtons("=")

        assertEquals("127,2", viewModel.result)
    }

    @Test
    fun percentage_directNumber() {
        val viewModel = CalculatorViewModel()
        // 50 %
        "50".forEach { viewModel.manageClickButtons(it.toString()) }
        viewModel.manageClickButtons("%")

        assertEquals("0,5", viewModel.result)
    }

    @Test
    fun percentage_addPercentage() {
        val viewModel = CalculatorViewModel()
        // 500 + 10 % = 550
        "500".forEach { viewModel.manageClickButtons(it.toString()) }
        viewModel.manageClickButtons("+")
        "10".forEach { viewModel.manageClickButtons(it.toString()) }
        viewModel.manageClickButtons("%")
        viewModel.manageClickButtons("=")

        assertEquals("550", viewModel.result)
    }
}