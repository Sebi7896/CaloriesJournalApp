Calories Journal

An Android app, written in Java, for tracking the calories you eat and burn each day and comparing them with your own goal ranges.

Features
Meals journal: add a meal with its date, calories, a short description and whether it was healthy or junk. Meals are grouped by day in an expandable list.
Exercises journal: add an exercise with its name, date, calories burned, description, duration (minutes and seconds) and a difficulty rating. Exercises are grouped by day, with today's entries labelled "Today".
Daily goals: set a minimum and maximum for calories eaten and for calories burned. The defaults are 1200 to 1800 for meals and 300 to 600 for exercises.
Stats dashboard: the home screen lists every day, newest first, with calories burned ("minus") and eaten ("plus") next to the goal ranges. Each day is color-coded as good (both in range), okay (one in range), bad (neither in range) or unknown (missing data or no goals set).
Input validation: required fields, positive whole numbers, a minimum goal that is not above the maximum, and no future dates.
Safe navigation: an "unsaved changes" dialog when leaving a form, a confirmation dialog before resetting all data, and a back-to-top button on the stats list.
Rotation-safe: the app state survives screen rotation through onSaveInstanceState.
Screens
Screen	Class	Purpose
Home / stats	MainActivity	Shows daily stats, opens the journals, resets all data
Meals	MealsActivity	Lists meals by day and edits the meal calorie goals
Add meal	AddMealActivity	Form with a Material date picker, calories, healthy yes/no and description
Exercises	ExercisesActivity	Lists exercises by day and edits the exercise calorie goals
Add exercise	AddExerciseActivity	Form with a date picker, calories, number pickers for the time, and a rating bar
How it works

The model classes live in the domain package: Meal, Exercise, Stats (all Serializable) and DateConverter. Meals and exercises are kept in memory in maps keyed by date. The screens exchange them through Intent extras and ActivityResultLauncher (StartActivityForResult). MainActivity combines the two maps into one Stats entry per day and picks the color from the goal ranges.

Tech stack
Java 11
Android SDK: minSdk 28, compileSdk and targetSdk 34
AndroidX AppCompat and ConstraintLayout
Material Components (MaterialDatePicker, TextInputLayout, floating action buttons)
ExpandableListView with custom BaseExpandableListAdapter classes
Gradle with a version catalog (gradle/libs.versions.toml)
Getting started
Clone the repository: git clone https://github.com/Sebi7896/CaloriesJournalApp.git
Open the project in Android Studio and let Gradle sync.
Run the app configuration on an emulator or a device with Android 9 (API 28) or newer.
Project structure
app/src/main/
├── java/
│   ├── eu/ase/ro/caloriesjournalapp/   # the five activities
│   └── domain/                         # Meal, Exercise, Stats, DateConverter
└── res/
    ├── layout/                         # one layout per screen and list item
    ├── drawable/                       # icons and the colored stat backgrounds
    └── values/                         # strings, colors, themes
My contribution

This was a university team project. Each screen was built by a different team member, and my part is the Exercises screen: ExercisesActivity, its layouts (activity_exercises, exercises_expandable_list_group, exercises_expandable_list_item), the expandable list adapter with the description dialog and difficulty rating, and the exercise goal validation.

Limitations and next steps
Data is kept in memory only, so closing the app clears it. Storing it in a Room database is the next step.
Meals, exercises and goals are passed between screens as serialized maps. A ViewModel with LiveData would remove most of that plumbing.
The stats list is built by hand with TextView objects. A RecyclerView would be cleaner.
The goal and status logic has no unit tests yet. Only the default template tests are present.
