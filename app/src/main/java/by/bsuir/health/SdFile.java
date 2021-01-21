package by.bsuir.health;

import android.graphics.Bitmap;

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
        this.description = DateCustom.getParseTime(description);
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
}

