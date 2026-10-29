package com.example.miprimerapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Vista que muestra el historial de operaciones aritméticas realizadas
@Composable
fun ShowHistory(
    navController: NavController,
    historyList: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .safeDrawingPadding()
            .padding(16.dp)
    ) {
        // Botón para regresar a la calculadora
        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.control_button))
        ) {
            Text(text = stringResource(R.string.back_button), color = colorResource(R.color.black))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Título de la pantalla de historial
        Text(
            text = stringResource(R.string.history_title),
            fontSize = 32.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Validación de estado vacío si no hay operaciones registradas
        if (historyList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.empty_history),
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
        } else {
            // Lista desplazable con el historial de operaciones
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(historyList) { item ->
                    Text(
                        text = item,
                        fontSize = 24.sp,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}