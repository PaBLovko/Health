package by.bsuir.health.controller;

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
}
