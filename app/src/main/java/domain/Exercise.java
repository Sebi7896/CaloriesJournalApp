package domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

public class Exercise implements Serializable {

    private String name;
    private Date date;
    private int calories;
    private String description;
    private int seconds;
    private float difficulty;

    public Exercise(String name, Date date, int calories, String description, int seconds, float difficulty) {
        this.name = name;
        this.date = date;
        this.calories = calories;
        this.description = description;
        this.seconds = seconds;
        this.difficulty = difficulty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public float getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(float difficulty) {
        this.difficulty = difficulty;
    }
    public static String getFormattedExerciseTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;

        return String.format(Locale.US, "%02dm:%02ds", minutes, remainingSeconds);
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "name='" + name + '\'' +
                ", date=" + date +
                ", calories=" + calories +
                ", description='" + description + '\'' +
                ", seconds=" + seconds +
                ", difficulty=" + difficulty +
                '}';
    }
}
