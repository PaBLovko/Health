package by.bsuir.health;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Pablo on 20.01.2021
 * @project Health
 */

public class SdFile {
    public static final String LOG_TAG = "myLogs";
    public static final String FILENAME = "file";
    public static final String DIR_SD = "MyFiles";
    public static final String FILENAME_SD = "fileSD";

    private int     Image;
    private String  name;
    private String  Description;

    public SdFile(int image, String name, String description) {
        Image = image;
        this.name = name;
        Description = description;
    }

    public int getImage() {
        return Image;
    }

    public void setImage(int image) {
        Image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    boolean cardAvailable(){        // проверяем доступность SD
        //Log.d(TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    void writeFileSD() {
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, FILENAME_SD);
        try {
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            // пишем данные
            bw.write("Содержимое файла на SD");
            // закрываем поток
            bw.close();
//            Log.d(TAG, "Файл записан на SD: " + sdFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
