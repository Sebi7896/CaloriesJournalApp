package domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {
    public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
    public static final SimpleDateFormat FANCY_FORMATTER = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.US);

    public static Date toDate(String string) {
        try {
            return FORMATTER.parse(string);
        } catch (ParseException e) {
            return null;
        }
    }
    public static String fromDate(Date date) {
        return FORMATTER.format(date);
    }

    public static String fancyFromDate(Date date) {
        return FANCY_FORMATTER.format(date);
    }
}
