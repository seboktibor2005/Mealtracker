package com.example.mealtracker.logic

/**
 * The fixed list of foods this app tracks. There's no "add food" flow -
 * this list is the single source of truth for which foods exist, and
 * FoodRepository merges saved state onto it.
 */
object FoodCatalog {

    data class Def(val id: String, val prepAheadOneDay: Boolean)

    val ALL: List<Def> = listOf(
        Def("Bolonyai", prepAheadOneDay = false),
        Def("Rizses", prepAheadOneDay = false),
        Def("Spinach", prepAheadOneDay = false),
        Def("Hus", prepAheadOneDay = false),
        Def("Krumpli", prepAheadOneDay = true),
        Def("Skyr", prepAheadOneDay = true),
        Def("Malna", prepAheadOneDay = true),
        Def("Szeder", prepAheadOneDay = true),
        Def("Quark", prepAheadOneDay = false),
        Def("Sajt", prepAheadOneDay = false),
        Def("Tomato", prepAheadOneDay = false)
    )
}
