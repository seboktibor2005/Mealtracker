package com.example.mealtracker.logic

import com.example.mealtracker.model.Food
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

/**
 * All date/stock math lives here, kept separate from UI so it can be
 * reasoned about (and tested) on its own.
 *
 * Base model: you eat exactly 1 portion per day, at a fixed time.
 * "Remaining on day D" = quantity - (number of eat-times that have already
 * passed between the effective start date and D, inclusive).
 *
 * Prep-ahead foods (prepAheadOneDay = true): these are prepared the day
 * before they're eaten (e.g. thawed/soaked overnight). That means the
 * portion eaten on day D was actually pulled from stock on day D-1, which
 * shifts the whole depletion timeline later by one day compared to the
 * naive same-day model. We implement that shift by simply treating the
 * food's start date as one day later than when it was actually added.
 */
object FoodCalculations {

    /** How many portions of [food] are left as of [date], given the current moment [now]. */
    fun remainingOn(food: Food, date: LocalDate, now: LocalDateTime = LocalDateTime.now()): Int {
        val addedDate = LocalDate.ofEpochDay(food.addedDateEpochDay)
        val effectiveStart = if (food.prepAheadOneDay) addedDate.plusDays(1) else addedDate

        // Before this food's effective start, treat it as full stock (not yet started).
        if (date.isBefore(effectiveStart)) return food.quantity

        var daysElapsed = ChronoUnit.DAYS.between(effectiveStart, date).toInt()

        // If we're asking about today and the eat-time hasn't happened yet today,
        // today doesn't count as consumed yet.
        val eatTime = LocalTime.of(food.eatTimeHour, food.eatTimeMinute)
        if (date.isEqual(now.toLocalDate()) && now.toLocalTime().isBefore(eatTime)) {
            daysElapsed -= 1
        }

        if (daysElapsed < 0) daysElapsed = 0
        return (food.quantity - daysElapsed).coerceAtLeast(0)
    }

    /** Whether you still have any of [food] left on [date]. */
    fun hasFoodOn(food: Food, date: LocalDate, now: LocalDateTime = LocalDateTime.now()): Boolean =
        remainingOn(food, date, now) > 0

    /** Days from "now" until [food] runs out (0 means refill is needed today/already). */
    fun daysUntilOut(food: Food, now: LocalDateTime = LocalDateTime.now()): Int =
        remainingOn(food, now.toLocalDate(), now)

    /**
     * True when [food] is a prep-ahead food and today is exactly the day you
     * need to buy more: you have one portion left (today's), but nothing to
     * prep tonight for tomorrow. Fires one day earlier than a plain
     * "refill needed" would, since prepping happens the night before eating.
     */
    fun needsBuyWarning(food: Food, date: LocalDate, now: LocalDateTime = LocalDateTime.now()): Boolean {
        if (!food.prepAheadOneDay) return false
        return remainingOn(food, date, now) == 1
    }
}
