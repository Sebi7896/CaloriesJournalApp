# Calories Journal – Android App

Calories Journal is a standalone Android application, written in Java, for tracking the calories you eat and burn each day and comparing them with your own goal ranges.

This project was developed as a university team project at the **Academy of Economic Studies of Bucharest (ASE)**, Faculty of Cybernetics, Statistics and Economic Informatics.

---

## 🎯 Project Objectives

- Track daily calories eaten (meals) and burned (exercises) in one place
- Compare every day with personal goal ranges
- Give quick visual feedback through color-coded daily stats
- Keep data entry simple and validated
- Practice core Android concepts: multiple activities, Intents and activity results, forms, lists and dialogs

---

## 🧱 System Architecture

### Mobile Client (Android)
- Java 11
- Android SDK (`minSdk 28`, `compileSdk` / `targetSdk 34`)
- AndroidX AppCompat & ConstraintLayout
- Material Components (`MaterialDatePicker`, `TextInputLayout`, floating action buttons)
- `ExpandableListView` with custom `BaseExpandableListAdapter` classes
- Gradle with a version catalog

### Application Structure
- Five activities: `MainActivity`, `MealsActivity`, `AddMealActivity`, `ExercisesActivity`, `AddExerciseActivity`
- Model classes in the `domain` package: `Meal`, `Exercise`, `Stats`, `DateConverter`
- Data kept in memory in maps keyed by date, passed between screens through `Intent` extras and `ActivityResultLauncher`

### Backend & External Services
- None. The app is fully standalone and does not use the network.

---

## 📱 Main Features

### Meals Journal
- Add meals with date, calories, a short description and a healthy / junk tag
- Meals grouped by day in an expandable list
- Daily calorie goal range for meals (default 1200 – 1800)
- Future dates are not allowed

### Exercises Journal
- Add exercises with name, date, calories burned, description, duration (minutes and seconds) and a difficulty rating
- Exercises grouped by day, with today's entries labelled "Today"
- Exercise description shown in a dialog
- Daily goal range for calories burned (default 300 – 600)

### Stats Dashboard
- One entry per day, newest first, showing calories burned ("minus") and eaten ("plus") next to the goal ranges
- Color-coded status: **good** (both in range), **okay** (one in range), **bad** (neither in range), **unknown** (missing data or no goals set)
- Back-to-top button and a reset option with a confirmation dialog

### Validation & Navigation
- Required fields, positive whole numbers and a minimum goal that is not above the maximum
- "Unsaved changes" warning when leaving a form
- State survives screen rotation through `onSaveInstanceState`

---

## 🗄️ Data Model

Main classes (all `Serializable`):

- Meal
- Exercise
- Stats
- DateConverter (date formatting helper)

Data is currently held in memory only. There is no database yet.

---

## 🚀 Running the Project

### Mobile Application
1. Clone the repository: `git clone https://github.com/Sebi7896/CaloriesJournalApp.git`
2. Open the project in Android Studio
3. Sync Gradle dependencies
4. Run on an emulator or a physical device with Android 9 (API 28) or newer

No API keys or backend setup are needed.

---

## 👥 Team & Contribution

This was a team project. Each screen was built by a different team member. My part is the **Exercises screen**:

- `ExercisesActivity` and its layouts (`activity_exercises`, `exercises_expandable_list_group`, `exercises_expandable_list_item`)
- The expandable list adapter with the description dialog and difficulty rating
- Validation of the exercise calorie goals

---

## 🎓 Academic Context

- **University:** Academy of Economic Studies of Bucharest
- **Faculty:** Cybernetics, Statistics and Economic Informatics
- **Specialization:** Informatica Economică
- **Year:** 2025

---

## 🔮 Future Improvements

- Persistent storage with a Room database
- `ViewModel` and `LiveData` instead of passing serialized maps between screens
- `RecyclerView` for the stats list
- Unit tests for the goal and status logic
- Weekly summaries and charts

---

## 📄 License

This project was developed strictly for educational and academic purposes.
