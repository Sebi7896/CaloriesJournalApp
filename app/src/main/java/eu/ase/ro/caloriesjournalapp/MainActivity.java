package eu.ase.ro.caloriesjournalapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.Instant;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import domain.DateConverter;
import domain.Exercise;
import domain.Meal;
import domain.Stats;

public class MainActivity extends AppCompatActivity {
    public final static String STATS_TREEMAP_KEY = "STATS_TREEMAP_KEY";
    private Map<Date, List<Exercise>> exercisesMap;
    private int exercisesGoalMax;
    private int exercisesGoalMin;
    private final int defaultExercisesGoalMax = 600;
    private final int defaultExercisesGoalMin = 300;
    private Map<Date, List<Meal>> mealsMap;
    private int mealsGoalMax;
    private int mealsGoalMin;
    private final int defaultMealsGoalMax = 1800;
    private final int defaultMealsGoalMin = 1200;
    private Intent intent;
    private ScrollView scrollView;
    private TextView tvCurrentDate;
    private ImageButton ibExercises;
    private ImageButton ibMeals;
    private LinearLayout llStats;
    private FloatingActionButton fabBackToTop;
    private MaterialButton fabReset;
    private ActivityResultLauncher<Intent> launcher;
    private Map<Date, Stats> statsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.gurau_adrian_florin_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeComponents();

        if (savedInstanceState == null) {
            setDefaultGoals();
            exercisesMap = new HashMap<>();
            mealsMap = new HashMap<>();
            statsMap = new TreeMap<>(new Comparator<Date>() {
                @Override
                public int compare(Date date1, Date date2) {
                    if (date1 == null && date2 == null) {
                        return 0;
                    } else if (date1 == null) {
                        return 1;
                    } else if (date2 == null) {
                        return -1;
                    }
                    return date2.compareTo(date1);
                }
            });
        } else {
            exercisesGoalMax = savedInstanceState.getInt(ExercisesActivity.GOAL_MAX_EXERCISE_KEY, 0);
            exercisesGoalMin = savedInstanceState.getInt(ExercisesActivity.GOAL_MIN_EXERCISE_KEY, 0);
            exercisesMap = (Map<Date, List<Exercise>>) savedInstanceState.getSerializable(ExercisesActivity.EXERCISE_HASHMAP_KEY);
            mealsGoalMax = savedInstanceState.getInt(MealsActivity.GOAL_MAX_MEAL_KEY, 0);
            mealsGoalMin = savedInstanceState.getInt(MealsActivity.GOAL_MIN_MEAL_KEY, 0);
            mealsMap = (Map<Date, List<Meal>>) savedInstanceState.getSerializable(MealsActivity.MEAL_HASHMAP_KEY);
            statsMap = (TreeMap<Date, Stats>) savedInstanceState.getSerializable(STATS_TREEMAP_KEY);
            refreshVisualStats(statsMap, exercisesGoalMin, exercisesGoalMax, mealsGoalMin, mealsGoalMax);
        }

        refreshResetButtonVisibility();
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), getMapCallback());
    }

    private void refreshResetButtonVisibility() {
        if (!statsMap.isEmpty() || exercisesGoalMax != defaultExercisesGoalMax ||
        exercisesGoalMin != defaultExercisesGoalMin || mealsGoalMax != defaultMealsGoalMax ||
        mealsGoalMin != defaultMealsGoalMin) {
            fabReset.setVisibility(View.VISIBLE);
        } else {
            fabReset.setVisibility(View.INVISIBLE);
        }
    }

    private void setDefaultGoals() {
        exercisesGoalMax = defaultExercisesGoalMax;
        exercisesGoalMin = defaultExercisesGoalMin;
        mealsGoalMax = defaultMealsGoalMax;
        mealsGoalMin = defaultMealsGoalMin;
    }

    @Override
    protected void onSaveInstanceState(Bundle toBeSavedState) {
        super.onSaveInstanceState(toBeSavedState);
        toBeSavedState.putInt(ExercisesActivity.GOAL_MAX_EXERCISE_KEY, exercisesGoalMax);
        toBeSavedState.putInt(ExercisesActivity.GOAL_MIN_EXERCISE_KEY, exercisesGoalMin);
        toBeSavedState.putSerializable(ExercisesActivity.EXERCISE_HASHMAP_KEY, (HashMap<Date, List<Exercise>>) exercisesMap);
        toBeSavedState.putInt(MealsActivity.GOAL_MAX_MEAL_KEY, mealsGoalMax);
        toBeSavedState.putInt(MealsActivity.GOAL_MIN_MEAL_KEY, mealsGoalMin);
        toBeSavedState.putSerializable(MealsActivity.MEAL_HASHMAP_KEY, (HashMap<Date, List<Meal>>) mealsMap);
        toBeSavedState.putSerializable(STATS_TREEMAP_KEY, (TreeMap<Date, Stats>) statsMap);
    }

    private ActivityResultCallback<ActivityResult> getMapCallback() {
        return result -> {
            switch (result.getResultCode()) {
                case ExercisesActivity.EXERCISE_RESULT_CODE: {
                    if (result.getData() != null) {
                        exercisesMap = (HashMap<Date, List<Exercise>>) result.getData().getSerializableExtra(ExercisesActivity.EXERCISE_HASHMAP_KEY);
                        exercisesGoalMax = result.getData().getIntExtra(ExercisesActivity.GOAL_MAX_EXERCISE_KEY, 0);
                        exercisesGoalMin = result.getData().getIntExtra(ExercisesActivity.GOAL_MIN_EXERCISE_KEY, 0);
                        populateStatsWithExercises(exercisesMap);
                    }
                } break;
                case MealsActivity.MEAL_RESULT_CODE: {
                    if (result.getData() != null) {
                        mealsMap = (HashMap<Date, List<Meal>>) result.getData().getSerializableExtra(MealsActivity.MEAL_HASHMAP_KEY);
                        mealsGoalMax = result.getData().getIntExtra(MealsActivity.GOAL_MAX_MEAL_KEY, 0);
                        mealsGoalMin = result.getData().getIntExtra(MealsActivity.GOAL_MIN_MEAL_KEY, 0);
                        populateStatsWithMeals(mealsMap);
                    }
                } break;
            }
            refreshVisualStats(statsMap, exercisesGoalMin, exercisesGoalMax, mealsGoalMin, mealsGoalMax);
            refreshResetButtonVisibility();
        };
    }

    private void populateStatsWithExercises(Map<Date, List<Exercise>> exercisesMap) {
        exercisesMap.forEach((date, stats) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date modifiedDate = calendar.getTime();

            int caloriesMinus = 0;
            for (Exercise exercise : exercisesMap.get(date)) {
                caloriesMinus += exercise.getCalories();
            }
            if (statsMap.get(modifiedDate) != null) {
                statsMap.get(modifiedDate).setCaloriesMinus(caloriesMinus);
            } else {
                statsMap.put(modifiedDate, new Stats(date, -1, caloriesMinus));
            }
        });
    }

    private void populateStatsWithMeals(Map<Date, List<Meal>> mealsMap) {
        mealsMap.forEach((date, stats) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date modifiedDate = calendar.getTime();

            int caloriesPlus = 0;
            for (Meal meal : mealsMap.get(date)) {
                caloriesPlus += meal.getCalories();
            }
            if (statsMap.get(modifiedDate) != null) {
                statsMap.get(modifiedDate).setCaloriesPlus(caloriesPlus);
            } else {
                statsMap.put(modifiedDate, new Stats(date, caloriesPlus, -1));
            }
        });
    }

    private void refreshVisualStats(Map<Date, Stats> statsMap, int goalMinExercises, int goalMaxExercises, int goalMinMeals, int goalMaxMeals) {
        llStats.removeAllViews();

        statsMap.forEach((date, stats) -> {
            TextView textView = createStatsTextView(stats, goalMinExercises, goalMaxExercises, goalMinMeals, goalMaxMeals);
            llStats.addView(textView);

            View separator = createSeparatorView();
            llStats.addView(separator);
        });
    }

    private TextView createStatsTextView(Stats stats, int goalMinExercises, int goalMaxExercises, int goalMinMeals, int goalMaxMeals) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView textView = new TextView(this);

        textView.setGravity(Gravity.CENTER);

        textView.setLayoutParams(layoutParams);

        String formattedDate = DateConverter.fancyFromDate(stats.getDate());
        String caloriesMinus = stats.getCaloriesMinus() >= 0 ? Integer.toString(stats.getCaloriesMinus()) : "N/A";
        String caloriesPlus = stats.getCaloriesPlus() >= 0 ? Integer.toString(stats.getCaloriesPlus()) : "N/A";
        String text = String.format(Locale.US,
                "%s\n\nCALORIES MINUS: %s [%d ; %d]\nCALORIES PLUS: %s [%d ; %d]",
                formattedDate,
                caloriesMinus,
                goalMinExercises, goalMaxExercises,
                caloriesPlus,
                goalMinMeals, goalMaxMeals
        );
        textView.setText(text);

        if ((goalMinMeals == 0 && goalMaxMeals == 0) ||
                (goalMinExercises == 0 && goalMaxExercises == 0) ||
                stats.getCaloriesMinus() == -1 || stats.getCaloriesPlus() == -1) {
            textView.setBackground(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.circular_text_view_main_unknown));
            textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
        else if ((goalMinMeals <= stats.getCaloriesPlus() && stats.getCaloriesPlus() <= goalMaxMeals) &&
                (goalMinExercises <= stats.getCaloriesMinus() && stats.getCaloriesMinus() <= goalMaxExercises)) {
            textView.setBackground(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.circular_text_view_main_good));
            textView.setTextColor(ContextCompat.getColor(this, R.color.Black));
        } else if ((goalMinMeals <= stats.getCaloriesPlus() && stats.getCaloriesPlus() <= goalMaxMeals) ||
                (goalMinExercises <= stats.getCaloriesMinus() && stats.getCaloriesMinus() <= goalMaxExercises)) {
            textView.setBackground(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.circular_text_view_main_okay));
            textView.setTextColor(ContextCompat.getColor(this, R.color.Black));
        } else {
            textView.setBackground(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.circular_text_view_main_bad));
            textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        }

        int leftPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        int topPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics());
        int rightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        int bottomPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics());
        textView.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        return textView;
    }

    private View createSeparatorView() {
        View view = new View(this);
        view.setMinimumHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);

        return view;
    }

    private void initializeComponents() {
        scrollView = findViewById(R.id.gurau_adrian_florin_main_scroll_view);
        tvCurrentDate = findViewById(R.id.gurau_adrian_florin_main_tv_currentDate);
        ibExercises = findViewById(R.id.gurau_adrian_florin_main_ib_exercises);
        ibMeals = findViewById(R.id.gurau_adrian_florin_main_ib_meals);
        llStats = findViewById(R.id.gurau_adrian_florin_main_ll_stats);
        fabBackToTop = findViewById(R.id.gurau_adrian_florin_main_fab_back_to_top);
        fabReset = findViewById(R.id.gurau_adrian_florin_main_fab_reset);

        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!scrollView.canScrollVertically(-1)) {
                    fabBackToTop.setVisibility(View.INVISIBLE);
                } else {
                    fabBackToTop.setVisibility(View.VISIBLE);
                }
            }
        });

        Date currentDate = Date.from(Instant.now());
        tvCurrentDate.setText(new StringBuilder("Current Date:\n").append(DateConverter.fancyFromDate(currentDate)));

        ibExercises.setOnClickListener(view -> {
            intent = new Intent(getApplicationContext(), ExercisesActivity.class);
            intent.putExtra(ExercisesActivity.EXERCISE_HASHMAP_KEY, (HashMap<Date, List<Exercise>>) exercisesMap);
            intent.putExtra(ExercisesActivity.GOAL_MAX_EXERCISE_KEY, exercisesGoalMax);
            intent.putExtra(ExercisesActivity.GOAL_MIN_EXERCISE_KEY, exercisesGoalMin);
            launcher.launch(intent);
        });

        ibMeals.setOnClickListener(view -> {
            intent = new Intent(getApplicationContext(), MealsActivity.class);
            intent.putExtra(MealsActivity.MEAL_HASHMAP_KEY, (HashMap<Date, List<Meal>>) mealsMap);
            intent.putExtra(MealsActivity.GOAL_MAX_MEAL_KEY, mealsGoalMax);
            intent.putExtra(MealsActivity.GOAL_MIN_MEAL_KEY, mealsGoalMin);
            launcher.launch(intent);
        });

        fabBackToTop.setOnClickListener(view -> {
            scrollView.smoothScrollTo(0, 0);
        });

        fabReset.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.main_reset_message)
                    .setTitle(R.string.main_reset_title)
                    .setPositiveButton(R.string.main_reset_yes, (dialog, id) -> {
                        exercisesMap.clear();
                        mealsMap.clear();
                        statsMap.clear();
                        exercisesMap = new HashMap<>();
                        mealsMap = new HashMap<>();
                        statsMap = new TreeMap<>(new Comparator<Date>() {
                            @Override
                            public int compare(Date date1, Date date2) {
                                if (date1 == null && date2 == null) {
                                    return 0;
                                } else if (date1 == null) {
                                    return 1;
                                } else if (date2 == null) {
                                    return -1;
                                }
                                return date2.compareTo(date1);
                            }
                        });
                        llStats.removeAllViews();
                        setDefaultGoals();
                        refreshResetButtonVisibility();
                        Toast.makeText(getApplicationContext(), R.string.main_data_reset_successfully, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(R.string.main_reset_no, (dialog, id) -> {
                        dialog.dismiss();
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }
}