package com.example.mealtracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.mealtracker.model.Food
import java.time.LocalDate

@Composable
fun FoodsScreen(foods: List<Food>, onUpdate: (Food) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Foods", style = MaterialTheme.typography.headlineSmall)
        foods.forEach { food ->
            FoodRow(food = food, onUpdate = onUpdate)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodRow(food: Food, onUpdate: (Food) -> Unit) {
    var quantityText by remember(food.id) { mutableStateOf(food.quantity.toString()) }
    var showTimePicker by remember { mutableStateOf(false) }
    val timeState = rememberTimePickerState(
        initialHour = food.eatTimeHour,
        initialMinute = food.eatTimeMinute,
        is24Hour = true
    )

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(food.id, style = MaterialTheme.typography.titleMedium)
                    if (food.prepAheadOneDay) {
                        Text(
                            "Prepped 1 day ahead",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = food.enabled,
                    onCheckedChange = { checked -> onUpdate(food.copy(enabled = checked)) }
                )
            }

            if (food.enabled) {
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eat time: %02d:%02d".format(food.eatTimeHour, food.eatTimeMinute))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { input -> quantityText = input.filter { it.isDigit() } },
                        label = { Text("Days you currently have") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val qty = quantityText.toIntOrNull() ?: 0
                            onUpdate(
                                food.copy(
                                    quantity = qty,
                                    addedDateEpochDay = LocalDate.now().toEpochDay()
                                )
                            )
                        }
                    ) {
                        Text("Restock")
                    }
                }
            }
        }
    }

    if (showTimePicker) {
        Dialog(onDismissRequest = { showTimePicker = false }) {
            Surface(shape = MaterialTheme.shapes.large) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = timeState)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onUpdate(
                                food.copy(
                                    eatTimeHour = timeState.hour,
                                    eatTimeMinute = timeState.minute
                                )
                            )
                            showTimePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}
