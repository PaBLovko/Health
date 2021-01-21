package by.bsuir.health;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author Pablo on 21.01.2021
 * @project Health
 */
public class DateCustom {
    public static String getParseTime(long timeInMillis) {
        final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        calendar.setTimeZone(TimeZone.getDefault());
        return format.format(calendar.getTime());
    }
}
