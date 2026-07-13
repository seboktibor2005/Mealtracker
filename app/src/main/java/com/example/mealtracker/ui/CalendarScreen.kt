package com.example.mealtracker.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mealtracker.logic.FoodCalculations
import com.example.mealtracker.model.Food
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// How many days forward to make available to scroll through.
private const val DAYS_AHEAD = 60

@Composable
fun CalendarScreen(foods: List<Food>) {
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

    val today = LocalDate.now()
    val now = LocalDateTime.now()
    val days = (0 until DAYS_AHEAD).map { today.plusDays(it.toLong()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(days) { day ->
            DayCard(day = day, today = today, foods = enabledFoods, now = now)
        }
    }
}

@Composable
private fun DayCard(day: LocalDate, today: LocalDate, foods: List<Food>, now: LocalDateTime) {
    val isToday = day.isEqual(today)
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = if (isToday) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = dayLabel(day, today),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            foods.forEach { food ->
                FoodStatusRow(food = food, day = day, now = now)
            }
        }
    }
}

private fun dayLabel(day: LocalDate, today: LocalDate): String {
    val datePart = day.format(DateTimeFormatter.ofPattern("MMM d"))
    return when {
        day.isEqual(today) -> "Today, $datePart"
        day.isEqual(today.plusDays(1)) -> "Tomorrow, $datePart"
        else -> day.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()) + ", $datePart"
    }
}

@Composable
private fun FoodStatusRow(food: Food, day: LocalDate, now: LocalDateTime) {
    val remaining = FoodCalculations.remainingOn(food, day, now)
    val hasFood = remaining > 0
    val buyWarning = FoodCalculations.needsBuyWarning(food, day, now)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = when {
                buyWarning -> Icons.Default.Warning
                hasFood -> Icons.Default.Check
                else -> Icons.Default.Close
            },
            contentDescription = if (hasFood) "Have it" else "Out of it",
            tint = when {
                buyWarning -> Color(0xFFF9A825)
                hasFood -> Color(0xFF2E7D32)
                else -> Color(0xFFC62828)
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(food.id, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = when {
                    buyWarning -> "Last one today - buy more, nothing to prep tonight!"
                    hasFood -> "$remaining day${if (remaining == 1) "" else "s"} left"
                    else -> "Refill needed"
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (buyWarning) Color(0xFFF9A825) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
