package by.bsuir.health.bean;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author Pablo on 20.01.2021
 * @project Health
 */

public class SdFile {
    private Bitmap  image;
    private String  name;
    private String  description;

    public SdFile(Bitmap image, String name, long description) {
        this.image = image;
        this.name = name;
        this.description = getParseTime(description);
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private static String getParseTime(long timeInMillis) {
        final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        calendar.setTimeZone(TimeZone.getDefault());
        return format.format(calendar.getTime());
    }
}

