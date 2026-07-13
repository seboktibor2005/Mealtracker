package com.example.mealtracker.model

import kotlinx.serialization.Serializable

/**
 * A tracked food item. The set of possible foods is fixed (see FoodCatalog) -
 * this class is just the per-food state: how much stock, when it's eaten,
 * and whether it's currently being tracked at all.
 *
 * Stored as plain primitives (not LocalDate/LocalTime) so it serializes to
 * JSON with kotlinx.serialization without needing custom serializers.
 *
 * - id: stable key matching a FoodCatalog entry (e.g. "Krumpli")
 * - quantity: how many days' worth of portions you currently have (1/day)
 * - eatTimeHour/eatTimeMinute: the time of day you eat this food
 * - addedDateEpochDay: the date (LocalDate.toEpochDay()) this stock count
 *   started - reset to "today" whenever you restock
 * - enabled: whether this food is currently being tracked/displayed
 * - prepAheadOneDay: true for foods you prepare the day before you eat them
 *   (e.g. defrosting/soaking overnight) - shifts the depletion timeline by
 *   one day and enables the early "buy today" warning
 */
@Serializable
data class Food(
    val id: String,
    val quantity: Int = 0,
    val eatTimeHour: Int = 12,
    val eatTimeMinute: Int = 0,
    val addedDateEpochDay: Long = 0,
    val enabled: Boolean = false,
    val prepAheadOneDay: Boolean = false
)
