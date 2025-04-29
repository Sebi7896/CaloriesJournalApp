package domain;

import java.io.Serializable;
import java.util.Date;

public class Meal implements Serializable {
    private Date date;
    private int calories;
    private String description;
    private Boolean isHealthy;

    public Meal(Date date, int calories, String description, Boolean isHealthy) {
        this.date = date;
        this.calories = calories;
        this.description = description;
        this.isHealthy = isHealthy;
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

    public Boolean getHealthy() {
        return isHealthy;
    }

    public void setHealthy(Boolean healthy) {
        isHealthy = healthy;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "date=" + date +
                ", calories=" + calories +
                ", description='" + description + '\'' +
                ", isHealthy=" + isHealthy +
                '}';
    }
}
