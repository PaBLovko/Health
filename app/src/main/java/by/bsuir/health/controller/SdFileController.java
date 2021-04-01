package by.bsuir.health.controller;

import android.Manifest;

/**
 * @author Pablo on 08.03.2021
 * @project Health
 */
public class SdFileController {
//    public ArrayList<DatabaseDimension> updateSdList(ArrayList<DatabaseDimension> databaseDimensions){
//        ArrayList<DatabaseDimension> sdFiles = new ArrayList<>();
//        for (DatabaseDimension file : files)
//            sdFiles.add(new DatabaseDimension(BitmapFactory.decodeFile(file.getAbsolutePath()), file.getName(),
//                    file.lastModified()));
//        return sdFiles;
//    }
//
//    public ArrayList<DatabaseDimension> getListFile(String storageDirectory) {
//       return new SdFileController().updateSdList(new Storage(storageDirectory).getFiles());
//    }

    public String[] addPermissions() {
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
    }
}
