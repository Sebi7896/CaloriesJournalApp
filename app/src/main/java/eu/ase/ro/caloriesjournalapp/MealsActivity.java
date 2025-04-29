package eu.ase.ro.caloriesjournalapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import domain.Meal;

public class MealsActivity extends AppCompatActivity {
    public static final String GOAL_MAX_MEAL_KEY = "GOAL_MAX_MEAL_KEY";
    public static final String GOAL_MIN_MEAL_KEY = "GOAL_MIN_MEAL_KEY";
    public static final String MEAL_HASHMAP_KEY = "MEAL_TREE_MAP_KEY";
    public static final int MEAL_RESULT_CODE = 200;

    public static final String DATES_KEY = "Dates_Key";
    ArrayList<Meal> meals = new ArrayList<>();
    private ActivityResultLauncher<Intent> launcher;
    private Button btnAddMeal;
    private MaterialButton backButton;

    private int caloriesGoalMin;
    private int caloriesGoalMax;

    private ExpandableListView elvMeals;
    private TreeMap<Date, ArrayList<Meal>> tmMeals;

    private TextInputEditText tietCaloriesGoalMin;
    private TextInputEditText tietCaloriesGoalMax;

    private Intent intent;
    private MealListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_meals);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.honciu_eduard_marian_meals), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadComponents();

        intent = getIntent();
        tietCaloriesGoalMin.setText(Integer.toString(intent.getIntExtra(GOAL_MIN_MEAL_KEY, 0)));
        tietCaloriesGoalMax.setText(Integer.toString(intent.getIntExtra(GOAL_MAX_MEAL_KEY, 0)));
        HashMap<Date, ArrayList<Meal>> hashMap = (HashMap<Date, ArrayList<Meal>>) intent.getSerializableExtra(MEAL_HASHMAP_KEY);

        tmMeals = new TreeMap<>(new Comparator<Date>() {
            @Override
            public int compare(Date o1, Date o2) {
                return -o1.compareTo(o2);
            }
        });

        tmMeals.putAll(hashMap);

        adapter = new MealListAdapter(MealsActivity.this, tmMeals);
        elvMeals.setAdapter(adapter);
        Intent intent = new Intent(getApplicationContext(), AddMealActivity.class);

        btnAddMeal.setOnClickListener( v -> {
            intent.putExtra(DATES_KEY, meals);
            launcher.launch(intent);
        });

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), getAddMealCallback());
    }

    private ActivityResultCallback<ActivityResult> getAddMealCallback() {
        return result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Meal meal = (Meal) result.getData().getSerializableExtra(AddMealActivity.MEAL_KEY);
                try {
                    Date mealDate = meal.getDate();
                    if (tmMeals.containsKey(mealDate)) {
                        tmMeals.get(mealDate).add(meal);
                    } else {
                        ArrayList<Meal> lMeal = new ArrayList<>();
                        lMeal.add(meal);
                        tmMeals.put(mealDate, lMeal);
                    }
                    MealListAdapter adapter = (MealListAdapter) elvMeals.getExpandableListAdapter();
                    adapter.notifyDataSetChanged();
                } catch (NullPointerException error) {
                    Toast.makeText(getApplicationContext(), R.string.meals_error_when_receiving_data, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }


    private void loadComponents(){
        btnAddMeal = findViewById(R.id.honciu_eduard_marian_btn_add_meal);
        btnAddMeal.setOnClickListener(getAddMealEvent());
        backButton = findViewById(R.id.honciu_eduard_marian_meal_fab_back);
        backButton.setOnClickListener(this::onClickListener);
        elvMeals = findViewById(R.id.honciu_eduard_marian_elv_meal_journal);
        elvMeals.setAdapter(adapter);
        tietCaloriesGoalMax = findViewById(R.id.honciu_eduard_marian_tiet_calories_goal_max);
        tietCaloriesGoalMin = findViewById(R.id.honciu_eduard_marian_tiet_calories_goal_min);
    }

    private void onClickListener(View view) {
        if (view.getId() == R.id.honciu_eduard_marian_meal_fab_back) {
            if(isCaloricRangeValid()) {
                Intent currentIntent = getIntent();
                currentIntent.putExtra(GOAL_MAX_MEAL_KEY, caloriesGoalMax);
                currentIntent.putExtra(GOAL_MIN_MEAL_KEY, caloriesGoalMin);
                currentIntent.putExtra(MEAL_HASHMAP_KEY, tmMeals);
                setResult(MEAL_RESULT_CODE, currentIntent);
                finish();
            }
        }
    }

    private View.OnClickListener getAddMealEvent() {
        return view -> {
            Intent intent = new Intent(getApplicationContext(), AddMealActivity.class);
            launcher.launch(intent);
        };
    }

    public boolean isCaloricRangeValid() {
        try {
            caloriesGoalMin = Integer.parseInt(tietCaloriesGoalMin.getText().toString());
            caloriesGoalMax = Integer.parseInt(tietCaloriesGoalMax.getText().toString());

            if (caloriesGoalMax < caloriesGoalMin) {
                tietCaloriesGoalMin.setError(getString(R.string.meals_lowerbound_highbound));
                return false;
            }
            if (caloriesGoalMin < 0) {
                tietCaloriesGoalMin.setError(getString(R.string.meals_value_must_be_positive));
                tietCaloriesGoalMax.setError(getString(R.string.meals_value_must_be_positive));
                return false;
            }
        } catch (NullPointerException | NumberFormatException e) {
            caloriesGoalMin = 0;
            caloriesGoalMax = 0;
            return true;
        }
        return true;
    }
}

class MealListAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final TreeMap<Date, ArrayList<Meal>> tmMeals;
    private final ArrayList<Date> listDataHeader;

    public MealListAdapter(Context context, TreeMap<Date, ArrayList<Meal>> tmMeals) {
        this.context = context;
        this.tmMeals = tmMeals;
        this.listDataHeader = new ArrayList<>(tmMeals.keySet());
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return tmMeals.get(listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return tmMeals.get(listDataHeader.get(groupPosition)).get(childPosition);
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
        String headerTitle = DateFormat.getDateInstance().format((Date) getGroup(groupPosition));
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.meals_expandable_list_item, null);
        }
        TextView lblListHeader = convertView.findViewById(R.id.honciu_eduard_marian_tv_lb_list_header);
        lblListHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Meal meal = (Meal) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.meals_expandable_list_group, null);
        }
        TextView caloriesTextView = convertView.findViewById(R.id.honciu_eduard_marian_tv_calories_list);
        TextView isHealthyTextView = convertView.findViewById(R.id.honciu_eduard_marian_tv_healthy_list);
        TextView descriptionTextView = convertView.findViewById(R.id.honciu_eduard_marian_tv_description_list);

        caloriesTextView.setText(String.valueOf(meal.getCalories()));
        isHealthyTextView.setText(meal.getHealthy() ? context.getString(R.string.meal_healthy) : context.getString(R.string.meals_junk));
        descriptionTextView.setText(String.valueOf(meal.getDescription()));

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        listDataHeader.clear();
        listDataHeader.addAll(tmMeals.keySet());
        super.notifyDataSetChanged();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
