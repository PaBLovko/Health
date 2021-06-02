package by.bsuir.kazhamiakin.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Pablo on 24.03.2021
 * @project Health
 */
public class DatabaseHelper {
    private static DatabaseDimension databaseDimension;
    public DatabaseHelper(){}

    public static void SaveToDB(String date, String time, ArrayList<Integer> data, int pulse,
                                int description, int numOfExtrasystole, int spo, String mode){
        String json = fromArrayToJson(data);
        databaseDimension = new DatabaseDimension(
                date, time, json, pulse, description, numOfExtrasystole, spo, mode);
        databaseDimension.save();
    }

    public static DatabaseDimension loadData(long id){
        databaseDimension = DatabaseDimension.findById(DatabaseDimension.class,id);
        return databaseDimension;
    }

    public static String fromArrayToJson(ArrayList<Integer> data){
        return new Gson().toJson(data);
    }

    public static ArrayList<Integer> fromJsonToArray(String data){
        Type type = new TypeToken<ArrayList<Integer>>(){}.getType();
        return new Gson().fromJson(data,type);
    }

    public static void deleteAll(){
        DatabaseDimension.deleteAll(DatabaseDimension.class);
    }

    public static void deleteItem(long id){
        databaseDimension = DatabaseDimension.findById(DatabaseDimension.class,id);
        databaseDimension.delete();
    }

    public static List<DatabaseDimension> getList(){
        return DatabaseDimension.listAll(DatabaseDimension.class);
    }

    public static List<DatabaseDimension> getPreparedData(){
        List<DatabaseDimension> dbList = getList();
        Collections.reverse(dbList);
        return dbList;
    }
}

