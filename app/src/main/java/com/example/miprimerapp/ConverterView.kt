package com.example.miprimerapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

// Vista de la pantalla de conversión de masa (miligramos, gramos, kilogramos, toneladas)
@Composable
fun ShowConverter(
    navController: NavController,
    viewModel: ConverterViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Botón para regresar a la pantalla anterior
        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.control_button))
        ) {
            Text(text = stringResource(R.string.back_button), color = colorResource(R.color.black))
        }

        // Título de la pantalla
        Text(
            text = stringResource(R.string.converter_title),
            fontSize = 32.sp,
            color = Color.White
        )

        // Campo de texto para ingresar el valor numérico
        OutlinedTextField(
            value = viewModel.inputNumber,
            onValueChange = { viewModel.onInputChanged(it) },
            label = { Text(stringResource(R.string.enter_value), color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.DarkGray
            )
        )

        // Selección de unidad de origen
        Text(text = stringResource(R.string.from_unit), color = Color.Gray)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("mg", "g", "kg", "t").forEach { unit ->
                Button(
                    onClick = { viewModel.updateFromUnit(unit) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.selectedFromUnit == unit) colorResource(R.color.operator_button) else colorResource(R.color.control_button)
                    )
                ) {
                    Text(text = unit, color = Color.White)
                }
            }
        }

        // Selección de unidad de destino
        Text(text = stringResource(R.string.to_unit), color = Color.Gray)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("mg", "g", "kg", "t").forEach { unit ->
                Button(
                    onClick = { viewModel.updateToUnit(unit) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.selectedToUnit == unit) colorResource(R.color.operator_button) else colorResource(R.color.control_button)
                    )
                ) {
                    Text(text = unit, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Visualización del resultado de la conversión
        Text(
            text = "${stringResource(R.string.result_label)}: ${viewModel.outputResult} ${viewModel.selectedToUnit}",
            fontSize = 28.sp,
            color = Color.White
        )
    }
}
