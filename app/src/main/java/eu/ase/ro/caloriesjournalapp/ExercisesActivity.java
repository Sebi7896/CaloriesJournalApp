package eu.ase.ro.caloriesjournalapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import domain.Exercise;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public class ExercisesActivity extends AppCompatActivity {
    public static final String GOAL_MAX_EXERCISE_KEY = "GOAL_MAX_EXERCISE_KEY";
    public static final String GOAL_MIN_EXERCISE_KEY = "GOAL_MIN_EXERCISE_KEY";
    public static final String EXERCISE_HASHMAP_KEY = "EXERCISE_TREE_MAP_KEY";
    public static final int EXERCISE_RESULT_CODE = 100;
    private MaterialButton backButton;
    private TextInputEditText lowerCaloriesGoalEditText;
    private TextInputEditText highCaloriesGoalEditText;
    private ExpandableListView exerciseExpandableListView;
    private Button addExerciseButton;
    private TreeMap<Date, List<Exercise>> exercisesSortedMap;
    private CustomExpandableListAdapter adapter;
    private Intent currentIntent;
    private ActivityResultLauncher<Intent> launcher;
    private int lowerCaloriesGoal;
    private int highCaloriesGoal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.iacob_sebastian_exercises), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initComponents();
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::onAddExerciseActivityResult);

        addExerciseButton.setOnClickListener(this::onClickListener);
        backButton.setOnClickListener(this::onClickListener);

        currentIntent = getIntent();
        lowerCaloriesGoalEditText.setText(Integer.toString(currentIntent.getIntExtra(GOAL_MIN_EXERCISE_KEY, 0)));
        highCaloriesGoalEditText.setText(Integer.toString(currentIntent.getIntExtra(GOAL_MAX_EXERCISE_KEY, 0)));
        HashMap<Date, List<Exercise>> hashMap = (HashMap<Date, java.util.List<Exercise>>) currentIntent.getSerializableExtra(ExercisesActivity.EXERCISE_HASHMAP_KEY);


        exercisesSortedMap = new TreeMap<>(new Comparator<Date>() {
            @Override
            public int compare(Date o1, Date o2) {
                return -o1.compareTo(o2);
            }
        });

        exercisesSortedMap.putAll(hashMap);

        adapter = new CustomExpandableListAdapter(ExercisesActivity.this, exercisesSortedMap);
        exerciseExpandableListView.setAdapter(adapter);

    }

    public void onAddExerciseActivityResult(ActivityResult result) {
        if(result.getResultCode() == RESULT_OK) {
            try {
                Exercise exercise = (Exercise) result.getData().getSerializableExtra(AddExerciseActivity.EXERCISE_OBJECT);
                if (exercisesSortedMap.keySet().stream().anyMatch(key -> CustomDateComparer.compare(key, exercise.getDate()))) {
                    Date date = exercisesSortedMap.keySet().stream().filter(key -> CustomDateComparer.compare(key, exercise.getDate())).findFirst().get();
                    exercisesSortedMap.get(date).add(exercise);
                } else {
                    List<Exercise> exerciseList = new ArrayList<>();
                    exerciseList.add(exercise);
                    exercisesSortedMap.put(exercise.getDate(), exerciseList);
                }
                adapter.notifyDataSetChanged();
            }catch (NullPointerException ignored) {}
        }
    }
    public void onClickListener(View view) {
        if (view.getId() == R.id.iacob_sebastian_exercise_toolbar_back_button) {
            if(isCaloricRangeValid()) {
                currentIntent = getIntent();
                currentIntent.putExtra(GOAL_MIN_EXERCISE_KEY, lowerCaloriesGoal);
                currentIntent.putExtra(GOAL_MAX_EXERCISE_KEY, highCaloriesGoal);
                currentIntent.putExtra(EXERCISE_HASHMAP_KEY, exercisesSortedMap);
                setResult(EXERCISE_RESULT_CODE, currentIntent);
                finish();
            }
        } else if (view.getId() == R.id.iacob_sebastian_exercise_add_exercise) {
            currentIntent = new Intent(getApplicationContext(), AddExerciseActivity.class);
            launcher.launch(currentIntent);
        }
    }
    public boolean isCaloricRangeValid() {
        try {
            lowerCaloriesGoal = Integer.parseInt(lowerCaloriesGoalEditText.getText().toString());
        } catch (Exception ex) {
            lowerCaloriesGoal = 0;
        }

        try {
            highCaloriesGoal = Integer.parseInt(highCaloriesGoalEditText.getText().toString());
        } catch (Exception ex) {
            highCaloriesGoal = 0;
        }

        if(highCaloriesGoal < lowerCaloriesGoal) {
            lowerCaloriesGoalEditText.setError(getString(R.string.exercises_list_lowerBound_highBound));
            return false;
        }

        if (highCaloriesGoal < 0) {
            lowerCaloriesGoalEditText.setError(getString(R.string.exercises_list_valoarea_trebuie_sa_fie_pozitiva));
            highCaloriesGoalEditText.setError(getString(R.string.exercises_list_valoarea_trebuie_sa_fie_pozitiva));
            return false;
        }

        return true;
    }
    public void initComponents() {
        backButton = findViewById(R.id.iacob_sebastian_exercise_toolbar_back_button);
        lowerCaloriesGoalEditText = findViewById(R.id.iacob_sebastian_exercise_lower_Calories_goal_editText);
        highCaloriesGoalEditText = findViewById(R.id.iacob_sebastian_High_Calories_goal_input_editText);
        exerciseExpandableListView = findViewById(R.id.iacob_sebastian_exercise_expList);
        addExerciseButton = findViewById(R.id.iacob_sebastian_exercise_add_exercise);
    }

}
class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<Date> listDataGroup;
    private final TreeMap<Date, List<Exercise>> listDataChild;

    public CustomExpandableListAdapter(Context context, TreeMap<Date, List<Exercise>> listDataChild) {
        this.context = context;
        this.listDataGroup = new ArrayList<>(listDataChild.keySet());
        this.listDataChild = listDataChild;
    }

    @Override
    public int getGroupCount() {
        return listDataGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        Date date = listDataGroup.get(groupPosition);
        return listDataChild.get(date).size();
    }


    @Override
    public Object getGroup(int groupPosition) {
        return listDataGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        Date dateGroup = listDataGroup.get(groupPosition);
        return listDataChild.get(dateGroup).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        final String headerTitle;
        if(CustomDateComparer.compare((Date)getGroup(groupPosition), Date.from(Instant.now()))) {
            headerTitle= "Today";
        }else {
            headerTitle = DateFormat.format("dd-MM-yyyy",((Date)getGroup(groupPosition))).toString();
        }
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
            convertView = inflater.inflate(R.layout.exercises_expandable_list_group, null);
        }
        TextView lblListHeader = convertView.findViewById(R.id.iacob_sebastian_lblListHeader);
        lblListHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Exercise childText =(Exercise) getChild(groupPosition,childPosition);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
            convertView = inflater.inflate(R.layout.exercises_expandable_list_item, null);
        }

        TextView exerciseName = convertView.findViewById(R.id.iacob_sebastian_exercise_groupList_nameTextView);
        exerciseName.setText(childText.getName());

        TextView exerciseCalories = convertView.findViewById(R.id.iacob_sebastian_exercise_groupList_caloriesTextView);
        exerciseCalories.setText(String.format(Locale.US, "%d cals", childText.getCalories()));

        Button exerciseDescription = convertView.findViewById(R.id.iacob_sebastian_exercise_groupList_descriptionButton);
        exerciseDescription.setOnClickListener(view -> {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle(childText.getName());
            alertDialog.setMessage(childText.getDescription());
            alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", (dialog, which) -> dialog.dismiss());
            alertDialog.show();
        });
        TextView exerciseDuration = convertView.findViewById(R.id.iacob_sebastian_exercise_groupList_durationTextView);
        exerciseDuration.setText(Exercise.getFormattedExerciseTime(childText.getSeconds()));
        RatingBar exerciseDifficulty = convertView.findViewById(R.id.iacob_sebastian_exercise_groupList_difficultyRatingBar);
        exerciseDifficulty.setRating(childText.getDifficulty());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
    @Override
    public void notifyDataSetChanged() {
        listDataGroup.clear();
        listDataGroup.addAll(listDataChild.keySet());
        super.notifyDataSetChanged();
    }
}
final class CustomDateComparer{
    public static boolean compare(Date date1, Date date2) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        return dateFormat.format(date1).compareTo(dateFormat.format(date2)) == 0;
    }
}