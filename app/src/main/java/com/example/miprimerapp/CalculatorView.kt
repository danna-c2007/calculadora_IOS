package com.example.miprimerapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// Vista principal de la calculadora con diseño estilo iOS
@Composable
fun ShowCalculator(
    navController: NavController,
    viewModel: CalculatorViewModel = viewModel(),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(10.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            // Fila de botones superiores para navegar a Convertidor e Historial
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { navController.navigate("converter") },
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.control_button)),
                    modifier = Modifier.size(50.dp)
                ) {
                    Text(text = stringResource(R.string.converter_symbol), color = colorResource(R.color.black))
                }

                Button(
                    onClick = { navController.navigate("history") },
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.control_button)),
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_history),
                        contentDescription = stringResource(R.string.history_title),
                        tint = colorResource(R.color.black),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            
            // Pantalla para mostrar la expresión matemática actual
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = viewModel.expression,
                fontSize = 28.sp,
                color = colorResource(R.color.expression_text),
                textAlign = TextAlign.End,
                softWrap = false,
                maxLines = 1
            )
            
            // Pantalla para mostrar el resultado actual
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = viewModel.result,
                fontSize = 45.sp,
                color = colorResource(R.color.white),
                textAlign = TextAlign.End,
                softWrap = false,
                maxLines = 1
            )
            
            // Cuadrícula de botones de la calculadora
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    ShowButton(R.string.delete_symbol, R.color.control_button, viewModel::manageClickButtons, R.color.black)
                    ShowButton(R.string.clear_symbol, R.color.control_button, viewModel::manageClickButtons, R.color.black)
                    ShowButton(R.string.percent_symbol, R.color.control_button, viewModel::manageClickButtons, R.color.black)
                    ShowButton(R.string.divide_symbol, R.color.operator_button, viewModel::manageClickButtons)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    ShowButton(R.string.digit_7, R.color.number_button, viewModel::manageClickButtons)
                    ShowButton(R.string.digit_8, R.color.number_button, viewModel::manageClickButtons)
                    ShowButton(R.string.digit_9, R.color.number_button, viewModel::manageClickButtons)
                    ShowButton(R.string.multiply_symbol, R.color.operator_button, viewModel::manageClickButtons)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    ShowButton(R.string.digit_4, R.color.number_button, viewModel::manageClickButtons)
                    ShowButton(R.string.digit_5, R.color.number_button, viewModel::manageClickButtons)
                    ShowButton(R.string.digit_6, R.color.number_button, viewModel::manageClickButtons)
                    ShowButton(R.string.subtraction_symbol, R.color.operator_button, viewModel::manageClickButtons)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    ShowButton(R.string.digit_1, R.color.number_button, viewModel::manageClickButtons)
                    ShowButton(R.string.digit_2, R.color.number_button, viewModel::manageClickButtons)
                    ShowButton(R.string.digit_3, R.color.number_button, viewModel::manageClickButtons)
                    ShowButton(R.string.add_symbol, R.color.operator_button, viewModel::manageClickButtons)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    ShowButton(R.string.sign_symbol, R.color.control_button, viewModel::manageClickButtons, R.color.black)
                    ShowButton(R.string.digit_0, R.color.number_button, viewModel::manageClickButtons)
                    ShowButton(R.string.comma_symbol, R.color.number_button, viewModel::manageClickButtons)
                    ShowButton(R.string.equals_symbol, R.color.operator_button, viewModel::manageClickButtons)
                }
            }
        }
    }
}

// Componente reutilizable para cada botón de la calculadora
@Composable
fun RowScope.ShowButton(
    @StringRes textId: Int,
    @ColorRes colorId: Int,
    onClick: (String) -> Unit,
    @ColorRes textColorId: Int = R.color.white
) {
    val text = stringResource(textId)

    Button(
        onClick = { onClick(text) },
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(colorId),
            contentColor = colorResource(textColorId)
        ),
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
    ) {
        Text(text = text, fontSize = 28.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    ShowCalculator(navController = rememberNavController())
}
