package by.bsuir.kazhamiakin.dao.storage;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import by.bsuir.kazhamiakin.exeption.storage.ConnectionStorageException;

/**
 * @author Pablo on 01.02.2021
 * @project Health
 */
public class Storage {
//    public static final String LOG_TAG = "myLogs";
//    public static final String FILENAME = "file";
    public static final String DIR_SD = "/MyFiles";
    public static final String FILENAME_SD = "fileSD";
    public static final String CONNECTION_STORAGE_EXCEPTION = "Sd Card don't supported";

    private final File sdFile;

    public Storage(String externalStorageDirectory){
        sdFile = new File(externalStorageDirectory);
        sdFile.mkdirs();
    }

    public File getSdFile() {
        return sdFile;
    }

    public static boolean cardAvailable(){        // проверяем доступность SD
        //Log.d(TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static String getExternalStorageDirectory() throws ConnectionStorageException {
        if(!Storage.cardAvailable())
            throw new ConnectionStorageException(CONNECTION_STORAGE_EXCEPTION);
        else return Environment.getExternalStorageDirectory().toString() + DIR_SD;
    }

    public void writeFileSD() throws ConnectionStorageException {
        if(!Storage.cardAvailable())
            throw new ConnectionStorageException(CONNECTION_STORAGE_EXCEPTION);
        File sdFile = new File(this.sdFile, FILENAME_SD);
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

    public ArrayList<File> getFiles(){
        File[] files = sdFile.listFiles(new ImageFileFilter());
        if (files.length == 0)
            return null;
        else return new ArrayList<>(Arrays.asList(files));
    }

    private static class ImageFileFilter implements FileFilter {
        private final String[] okFileExtensions = new String[] {"jpg", "png", "gif", "bmp"};
        public boolean accept(File file) {
            for (String extension : okFileExtensions)
                if (file.getName().toLowerCase().endsWith(extension)) return true;
            return false;
        }
    }
}
