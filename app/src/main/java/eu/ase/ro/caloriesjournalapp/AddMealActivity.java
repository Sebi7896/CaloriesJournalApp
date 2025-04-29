package eu.ase.ro.caloriesjournalapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;
import java.util.Date;
import domain.DateConverter;
import domain.Meal;

public class AddMealActivity extends AppCompatActivity {
    public static final String MEAL_KEY = "meal_key";
    private MaterialButton back;
    private Date selectedDate;
    private RadioGroup isHealthy_rg;
    private TextInputEditText description_tiet;
    private TextInputEditText calories_tiet;
    private MaterialDatePicker<Long> datePicker;
    private Button pickDateButton;
    private Button save_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_meal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.hachem_khaled_add_meal), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        loadComponents();
        back.setOnClickListener(v -> {
            if(fieldsChanged())
            {
                AlertDialog alertDialog = new AlertDialog.Builder(AddMealActivity.this).create();
                alertDialog.setTitle(getString(R.string.add_meal_unsaved_changes));
                alertDialog.setMessage(getString(R.string.add_meal_you_have_unsaved_changes_are_you_sure_you_want_to_go_back));
                alertDialog.setButton(Dialog.BUTTON_POSITIVE, getString(R.string.add_meal_yes), (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });
                alertDialog.setButton(Dialog.BUTTON_NEGATIVE, getString(R.string.add_meal_cancel), (dialog, which) -> dialog.dismiss());
                alertDialog.show();
            }
            else{
                finish();
            }
        });
        pickDateButton.setOnClickListener(v -> showDatePicker());
        save_btn.setOnClickListener(v -> {
            if (isValid()) {
                int calories = Integer.parseInt(calories_tiet.getText().toString());
                boolean isHealthy = false;
                int rbID = isHealthy_rg.getCheckedRadioButtonId();
                RadioButton selected_rb = findViewById(rbID);
                if (selected_rb.getText().toString().equals(getString(R.string.add_meal_yes))) {
                    isHealthy = true;
                }
                String description = description_tiet.getText().toString().trim();
                Meal meal = new Meal(selectedDate, calories, description, isHealthy);
                intent.putExtra(MEAL_KEY, meal);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void showDatePicker() {
        datePicker.show(getSupportFragmentManager(), getString(R.string.add_meal_date_picker));
    }

    private void loadComponents() {
        pickDateButton = findViewById(R.id.hachem_khaled_pdb);
        save_btn = findViewById(R.id.hachem_khaled_btn_save_meal);
        back = findViewById(R.id.hachem_khaled_exercise_toolbar_back_button);
        datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.add_meal_select_a_date)
                .setPositiveButtonText(R.string.add_meal_select)
                .setNegativeButtonText(R.string.add_meal_cancel)
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            if (selection != null) {
                Date date = new Date(selection);
                selectedDate = date;
                selectedDate = stripTimeFromDate(selectedDate);
                Date currentDate = new Date();
                if (selectedDate != null && selectedDate.compareTo(currentDate) > 0) {
                    Toast.makeText(getApplicationContext(), R.string.add_meal_you_cannot_select_a_future_date, Toast.LENGTH_SHORT).show();
                    selectedDate = null;
                }

                if (selectedDate != null) {
                    String formattedDate = DateConverter.fromDate(date);
                    Toast.makeText(getApplicationContext(), getString(R.string.add_meal_date_selected) + formattedDate, Toast.LENGTH_SHORT).show();
                }
            }
        });

        description_tiet = findViewById(R.id.hachem_khaled_tiet_meal_description);
        calories_tiet = findViewById(R.id.hachem_khaled_tiet_meal_calories);
        isHealthy_rg = findViewById(R.id.hachem_khaled_rg_is_healthy);
    }

    private Boolean isValid() {
        try {
            int calories = Integer.parseInt(calories_tiet.getText().toString());
            if (calories <= 0) {
                Toast.makeText(getApplicationContext(), R.string.add_meal_calories_must_be_more_than_0, Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.add_meal_invalid_number_format_for_calories, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedDate == null) {
            Toast.makeText(getApplicationContext(), R.string.add_meal_you_must_select_a_date, Toast.LENGTH_SHORT).show();
            return false;
        }

        int rbID = isHealthy_rg.getCheckedRadioButtonId();
        if (rbID == -1) {
            Toast.makeText(getApplicationContext(), R.string.add_meal_was_the_meal_healthy, Toast.LENGTH_SHORT).show();
            return false;
        }


        if (description_tiet.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.add_meal_description_missing, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private Boolean fieldsChanged() {
        if(!calories_tiet.getText().toString().trim().isEmpty()){
            return true;
        }
        if (selectedDate != null) {
            return true;
        }
        int rbID = isHealthy_rg.getCheckedRadioButtonId();
        if (rbID != -1) {
            return true;
        }
        if(!description_tiet.getText().toString().trim().isEmpty()){
            return true;
        }
        return false;
    }

    public static Date stripTimeFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
