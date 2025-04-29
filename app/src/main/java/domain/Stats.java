package domain;

import java.io.Serializable;
import java.util.Date;

public class Stats implements Serializable {
    private Date date;
    private int caloriesPlus;
    private int caloriesMinus;

    public Stats(Date date, int caloriesPlus, int caloriesMinus) {
        this.date = date;
        this.caloriesPlus = caloriesPlus;
        this.caloriesMinus = caloriesMinus;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCaloriesPlus() {
        return caloriesPlus;
    }

    public void setCaloriesPlus(int caloriesPlus) {
        this.caloriesPlus = caloriesPlus;
    }

    public int getCaloriesMinus() {
        return caloriesMinus;
    }

    public void setCaloriesMinus(int caloriesMinus) {
        this.caloriesMinus = caloriesMinus;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "date=" + date +
                ", caloriesPlus=" + caloriesPlus +
                ", caloriesMinus=" + caloriesMinus +
                '}';
    }
}