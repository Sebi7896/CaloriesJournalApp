package eu.ase.ro.caloriesjournalapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import domain.Exercise;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddExerciseActivity extends AppCompatActivity {

    private TextInputEditText txtName;
    private TextInputLayout txtNameLayout;
    private TextInputEditText txtCalories;
    private TextInputLayout txtCaloriesLayout;

    private Button btnCalendar;
    private TextView lblRating;
    private TextView lblTime;
    private TextInputEditText txtDate;
    private TextInputEditText txtDescription;
    private RatingBar ratingDifficulty;

    private DatePickerDialog pickerDate;
    private DatePickerDialog.OnDateSetListener pickerDateListener;

    private NumberPicker numberSeconds;
    private NumberPicker numberMinutes;

    private Button btnSave;
    private Button btnBack;
    private final Calendar calendar = Calendar.getInstance();

    public static final String EXERCISE_OBJECT = "exerciseObject";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_exercise);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tanasescu_robert_gabriel_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InitiateComponents();
        SetDefaultDate();
        SetNumberPickersValue();

        pickerDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                String myFormat="dd-MM-yyyy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                txtDate.setText(dateFormat.format(calendar.getTime()));
            }
        };

        ratingDifficulty.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating<1.0f) {
                    ratingBar.setRating(1.0f);
                    rating = 1.0f;
                }

                lblRating.setText(String.format(Locale.US, "%.2f", rating));
                lblRating.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.black));
            }
        });

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickerDate = new DatePickerDialog(AddExerciseActivity.this, pickerDateListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));

                pickerDate.getDatePicker().setMaxDate(new Date().getTime());
                pickerDate.show();
            }
        });

        txtName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (txtName.length() <= 0)
                        txtNameLayout.setError(getString(R.string.add_exercise_name_is_required));
                    else {
                        txtNameLayout.setErrorEnabled(false);
                        txtNameLayout.setError(null);
                    }
                }
            }
        });

        txtCalories.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String caloriesString = txtCalories.getText().toString();
                    int calories = 0;

                    if (txtCalories.length() == 0) {
                        txtCaloriesLayout.setError(getText(R.string.add_exercise_calories_field_is_required));
                    }
                    else {
                        try {
                            calories = Integer.parseInt(caloriesString);
                        }
                        catch (NumberFormatException e) {
                            txtCaloriesLayout.setError(getText(R.string.add_exercise_calories_value_is_not_integer));
                        }

                        if (calories <= 0)
                        {
                            txtCaloriesLayout.setError(getText(R.string.add_exercise_calories_value_must_be_positive));
                        }
                        else {
                            txtCaloriesLayout.setErrorEnabled(false);
                            txtCaloriesLayout.setError(null);
                        }
                    }
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IsValid()) {
                    int seconds = numberMinutes.getValue() * 60 + numberSeconds.getValue();
                    Exercise exercise = new Exercise(txtName.getText().toString(),
                            calendar.getTime(),
                            Integer.parseInt(txtCalories.getText().toString()),
                            txtDescription.getText().toString(),
                            seconds,
                            Float.parseFloat(lblRating.getText().toString()));


                    Intent intent = getIntent();
                    intent.putExtra(EXERCISE_OBJECT, exercise);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AreChangesMade()) {

                    AlertDialog alertDialog = new AlertDialog.Builder(AddExerciseActivity.this).create();
                    alertDialog.setTitle(getString(R.string.add_exercise_unsaved_changes_title));
                    alertDialog.setMessage(getString(R.string.add_exercise_you_have_unsaved_changes_are_you_sure_you_want_to_go_back));
                    alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Yes", (dialog, which) -> { dialog.dismiss(); finish(); });
                    alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> dialog.dismiss());
                    alertDialog.show();
                }
                else {
                    setResult(RESULT_CANCELED);
                    finish();
                }

            }
        });
    }

    private boolean IsValid() {
        boolean valid = true;

        if (txtName.length() <= 0) {
            txtNameLayout.setError(getString(R.string.add_exercise_name_is_required));
            valid = false;
        }
        else {
            txtNameLayout.setErrorEnabled(false);
            txtNameLayout.setError(null);
        }
        if(txtName.length() >=10) {
            txtNameLayout.setError(getString(R.string.add_exercise_name_is_too_long));
            valid = false;
        }else {
            txtNameLayout.setErrorEnabled(false);
            txtNameLayout.setError(null);
        }

        if (txtCalories.length() == 0) {
            txtCaloriesLayout.setError(getText(R.string.add_exercise_calories_field_is_required));
            valid = false;
        }
        else {
            String caloriesString = txtCalories.getText().toString();
            int calories = 0;
            try {
                calories = Integer.parseInt(caloriesString);
            }
            catch(NumberFormatException e)
            {
                txtCaloriesLayout.setError(getString(R.string.add_exercise_calories_value_is_not_integer));
                valid = false;
            }

            if (calories <= 0)
            {
                txtCaloriesLayout.setError(getString(R.string.add_exercise_calories_value_must_be_positive));
                valid = false;
            }
            else {
                txtCaloriesLayout.setErrorEnabled(false);
                txtCaloriesLayout.setError(null);
            }
        }

        if (lblRating.length() == 0 || lblRating.getCurrentTextColor() == ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_light))
        {
            lblRating.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_light));
            lblRating.setText(getText(R.string.add_exercise_rating_the_difficulty_is_required));
            valid = false;
        }
        else {
            lblRating.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.black));
        }

        if (numberMinutes.getValue() == 0 && numberSeconds.getValue() == 0) {
            lblTime.setTextColor(Color.RED);
            valid = false;
        }
        else
            lblTime.setTextColor(Color.BLACK);

        return valid;
    }

    private boolean AreChangesMade() {
        String myFormat="dd-MM-yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        String currentDate = dateFormat.format(Calendar.getInstance().getTime());

        if (txtName.length() != 0 || txtDate.getText().toString().equalsIgnoreCase(currentDate) == false ||
                txtCalories.length() != 0 || numberMinutes.getValue() != 0 || numberSeconds.getValue() != 0 ||
                lblRating.length() != 0 || txtDescription.length() != 0)
            return true;
        else
            return false;
    }

    private void SetNumberPickersValue() {
        numberMinutes.setMinValue(0);
        numberMinutes.setMaxValue(29);
        numberMinutes.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format(Locale.US,"%02d", i);
            }
        });

        numberSeconds.setMinValue(0);
        numberSeconds.setMaxValue(59);
        numberSeconds.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format(Locale.US, "%02d", i);
            }
        });
    }

    private void SetDefaultDate() {
        String myFormat="dd-MM-yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        txtDate.setText(dateFormat.format(calendar.getTime()));
    }
    private void InitiateComponents() {
        txtName = findViewById(R.id.tanasescu_robert_gabriel_txtName);
        txtNameLayout = findViewById(R.id.tanasescu_robert_gabriel_txtNameLayout);
        txtCalories = findViewById(R.id.tanasescu_robert_gabriel_txtCalories);
        txtCaloriesLayout = findViewById(R.id.tanasescu_robert_gabriel_txtCaloriesLayout);

        btnCalendar = findViewById(R.id.tanasescu_robert_gabriel_btnCalendar);
        lblRating = findViewById(R.id.tanasescu_robert_gabriel_lblRating);
        lblTime = findViewById(R.id.tanasescu_robert_gabriel_lblTime);
        txtDate = findViewById(R.id.tanasescu_robert_gabriel_txtDate);
        txtDescription = findViewById(R.id.tanasescu_robert_gabriel_txtDescription);
        ratingDifficulty = findViewById(R.id.tanasescu_robert_gabriel_ratingDifficulty);

        numberMinutes = findViewById(R.id.tanasescu_robert_gabriel_numberMinutes);
        numberSeconds = findViewById(R.id.tanasescu_robert_gabriel_numberSeconds);

        btnSave = findViewById(R.id.tanasescu_robert_gabriel_btnSave);
        btnBack = findViewById(R.id.tanasescu_robert_gabriel_exercise_toolbar_back_button);
    }
}