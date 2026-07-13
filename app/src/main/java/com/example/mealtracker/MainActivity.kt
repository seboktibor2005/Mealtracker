package com.example.mealtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.mealtracker.data.FoodRepository
import com.example.mealtracker.model.Food
import com.example.mealtracker.ui.CalendarScreen
import com.example.mealtracker.ui.FoodsScreen
import com.example.mealtracker.ui.StockScreen
import com.example.mealtracker.ui.theme.MealTrackerTheme
import kotlinx.coroutines.launch

private enum class Screen { Calendar, Stock, Foods }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = FoodRepository(applicationContext)

        setContent {
            MealTrackerTheme {
                MealTrackerApp(repository)
            }
        }
    }
}

@Composable
private fun MealTrackerApp(repository: FoodRepository) {
    val scope = rememberCoroutineScope()
    val foods by repository.foodsFlow.collectAsState(initial = emptyList<Food>())
    var screen by remember { mutableStateOf(Screen.Calendar) }

    val onUpdate: (Food) -> Unit = { food ->
        scope.launch { repository.updateFood(food) }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = screen == Screen.Calendar,
                    onClick = { screen = Screen.Calendar },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Today") },
                    label = { Text("Today") }
                )
                NavigationBarItem(
                    selected = screen == Screen.Stock,
                    onClick = { screen = Screen.Stock },
                    icon = { Icon(Icons.Default.Inventory2, contentDescription = "Stock") },
                    label = { Text("Stock") }
                )
                NavigationBarItem(
                    selected = screen == Screen.Foods,
                    onClick = { screen = Screen.Foods },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Foods") },
                    label = { Text("Foods") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (screen) {
                Screen.Calendar -> CalendarScreen(foods = foods)
                Screen.Stock -> StockScreen(foods = foods)
                Screen.Foods -> FoodsScreen(foods = foods, onUpdate = onUpdate)
            }
        }
    }
}
