# Meal Tracker

A minimal local Android app for tracking a fixed set of foods, their stock,
and when they run out.

## Tracked foods
Bolonyai, Rizses, Spinach, Hus, Krumpli, Skyr, Malna, Szeder, Quark, Sajt,
Tomato. This list is fixed in the app (see `FoodCatalog.kt`) — there's no
"add food" screen. Each food can individually be turned on/off, and only
foods that are turned on show up in the Today and Stock tabs.

## The three tabs

**Today** — starts at today and rolls forward day by day. For each day,
every enabled food shows whether you'll still have it, and how many days
are left. Today's card is outlined.

**Stock** — a constant, always-current snapshot: how much of each enabled
food you have right now, sorted most-urgent-first.

**Foods** — management screen. Per food: a toggle to turn tracking on/off,
a button to set the time of day you eat it, and a field + "Restock" button
to set how many days' worth you currently have (this resets the countdown
to start from today).

## How the "days left" logic works
You're assumed to eat exactly 1 portion per day, at the time you set.
- Before your eat-time on a given day: that day doesn't count as consumed yet.
- After your eat-time: that day counts as consumed.
- `remaining on day D = quantity - (days consumed since it was last restocked)`

This logic lives entirely in `FoodCalculations.kt`, separate from the UI.

### Prep-ahead foods
Krumpli, Skyr, Malna, and Szeder are prepared the day before you eat them
(e.g. thawed/soaked overnight). Because of that one-day lag:
- Their whole depletion timeline is shifted one day later than a same-day
  food with the same quantity (you don't actually start eating until the
  day after the first prep).
- When only 1 portion is left, the app shows a **"Buy today!"** warning
  instead of the normal countdown — because that portion is already
  prepped for today, and there's nothing left to prep tonight for
  tomorrow unless you restock today.

## How to open and run
1. Install [Android Studio](https://developer.android.com/studio).
2. Open this folder (`MealTracker/`) as a project — File → Open.
3. Let Gradle sync (first sync needs an internet connection to download
   dependencies).
4. Plug in an Android phone (USB debugging on) or start an emulator.
5. Click Run ▶.

Minimum Android version supported: Android 8.0 (API 26).

If you hit a `D8 OutOfMemoryError` during a build, see `gradle.properties`
at the project root — it already sets a larger Gradle heap
(`org.gradle.jvmargs`). Lower the `-Xmx` value there if your machine has
limited RAM.

## Building the APK without a local build (GitHub Actions)
`.github/workflows/build-apk.yml` builds the debug APK on GitHub's own
servers, so your machine's RAM is never a factor. To use it:

1. Create a new repository on GitHub (public or private, doesn't matter).
2. Push this project to it:
   ```bash
   cd MealTracker
   git init
   git add .
   git commit -m "Meal Tracker"
   git branch -M main
   git remote add origin https://github.com/<your-username>/<repo-name>.git
   git push -u origin main
   ```
3. Go to the repo's **Actions** tab on GitHub. The "Build APK" workflow
   starts automatically on push (or click **Run workflow** to trigger it
   manually anytime after).
4. Once it finishes (green checkmark), click into that run, scroll down to
   **Artifacts**, and download `MealTracker-debug-apk` — it's a zip
   containing `app-debug.apk`. Transfer that to your phone and install it.

Every time you push a change, a fresh APK is built automatically.

## Project structure
```
app/src/main/java/com/example/mealtracker/
├── MainActivity.kt          # entry point, bottom nav between the 3 tabs
├── model/Food.kt            # per-food state (quantity, eat time, on/off...)
├── logic/
│   ├── FoodCatalog.kt       # the fixed list of 11 foods + prep-ahead flags
│   └── FoodCalculations.kt  # all the date/stock math (no UI, no I/O)
├── data/FoodRepository.kt   # merges saved state onto the catalog via DataStore
└── ui/
    ├── CalendarScreen.kt    # "Today" tab - rolls forward from today
    ├── StockScreen.kt       # "Stock" tab - current amounts, always visible
    ├── FoodsScreen.kt       # "Foods" tab - toggle/eat-time/restock per food
    └── theme/Theme.kt
```
