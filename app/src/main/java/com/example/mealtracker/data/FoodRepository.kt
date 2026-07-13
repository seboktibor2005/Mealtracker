package com.example.mealtracker.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mealtracker.logic.FoodCatalog
import com.example.mealtracker.model.Food
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate

private val Context.dataStore by preferencesDataStore(name = "meal_tracker_prefs")

class FoodRepository(private val context: Context) {

    private val foodsKey = stringPreferencesKey("foods_json")
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Always emits exactly one Food per FoodCatalog entry, in catalog order:
     * saved state is merged onto the fixed catalog, and any catalog entry
     * with no saved state yet gets sensible defaults (off, empty, noon).
     */
    val foodsFlow: Flow<List<Food>> = context.dataStore.data.map { prefs ->
        val jsonString = prefs[foodsKey] ?: "[]"
        val stored = runCatching { json.decodeFromString<List<Food>>(jsonString) }.getOrDefault(emptyList())
        val byId = stored.associateBy { it.id }

        FoodCatalog.ALL.map { def ->
            byId[def.id]?.copy(prepAheadOneDay = def.prepAheadOneDay)
                ?: Food(
                    id = def.id,
                    quantity = 0,
                    eatTimeHour = 12,
                    eatTimeMinute = 0,
                    addedDateEpochDay = LocalDate.now().toEpochDay(),
                    enabled = false,
                    prepAheadOneDay = def.prepAheadOneDay
                )
        }
    }

    private suspend fun saveFoods(foods: List<Food>) {
        context.dataStore.edit { prefs ->
            prefs[foodsKey] = json.encodeToString(foods)
        }
    }

    /** Replaces the single food matching [updated.id], leaving the rest untouched. */
    suspend fun updateFood(updated: Food) {
        val current = foodsFlow.first()
        saveFoods(current.map { if (it.id == updated.id) updated else it })
    }
}
