package com.example.mealtracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mealtracker.logic.FoodCalculations
import com.example.mealtracker.model.Food
import java.time.LocalDateTime

/**
 * Always-on snapshot of current stock, independent of the calendar.
 * Sorted most-urgent-first so what needs attention surfaces at the top.
 */
@Composable
fun StockScreen(foods: List<Food>) {
    val enabledFoods = foods.filter { it.enabled }

    if (enabledFoods.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "No foods turned on yet.\nGo to the Foods tab to turn some on.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    val now = LocalDateTime.now()
    val sorted = enabledFoods.sortedBy { FoodCalculations.daysUntilOut(it, now) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(sorted, key = { it.id }) { food ->
            StockRow(food = food, now = now)
        }
    }
}

@Composable
private fun StockRow(food: Food, now: LocalDateTime) {
    val remaining = FoodCalculations.daysUntilOut(food, now)
    val buyWarning = FoodCalculations.needsBuyWarning(food, now.toLocalDate(), now)

    val (statusText, color) = when {
        buyWarning -> "Buy today!" to Color(0xFFF9A825)
        remaining <= 0 -> "Refill needed" to Color(0xFFC62828)
        else -> "$remaining day${if (remaining == 1) "" else "s"} left" to Color(0xFF2E7D32)
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(food.id, style = MaterialTheme.typography.titleMedium)
                Text(
                    "Eaten at %02d:%02d".format(food.eatTimeHour, food.eatTimeMinute),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                statusText,
                style = MaterialTheme.typography.titleMedium,
                color = color
            )
        }
    }
}
