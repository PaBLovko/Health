package by.bsuir.health.controller;

import android.Manifest;
import android.graphics.BitmapFactory;

import java.io.File;
import java.util.ArrayList;

import by.bsuir.health.bean.SdFile;
import by.bsuir.health.dao.storage.Storage;

/**
 * @author Pablo on 08.03.2021
 * @project Health
 */
public class SdFileController {
    public ArrayList<SdFile> updateSdList(ArrayList<File> files){
        ArrayList<SdFile> sdFiles = new ArrayList<>();
        for (File file : files)
            sdFiles.add(new SdFile(BitmapFactory.decodeFile(file.getAbsolutePath()), file.getName(),
                    file.lastModified()));
        return sdFiles;
    }

    public ArrayList<SdFile> getListFile(String storageDirectory) {
       return new SdFileController().updateSdList(new Storage(storageDirectory).getFiles());
    }

    public String[] addPermissions() {
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
    }
}
