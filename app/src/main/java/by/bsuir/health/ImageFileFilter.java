package by.bsuir.health;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Pablo on 20.01.2021
 * @project Health
 */
public class ImageFileFilter implements FileFilter {
    private final String[] okFileExtensions = new String[] {"jpg", "png", "gif", "bmp"};

    public boolean accept(File file) {
        for (String extension : okFileExtensions)
            if (file.getName().toLowerCase().endsWith(extension)) return true;
        return false;
    }
}
