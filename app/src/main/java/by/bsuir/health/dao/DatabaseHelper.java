package by.bsuir.health.dao;

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

    public static void SaveToDB(String date, String time, ArrayList<Integer> dataECG,
                                int pulse, int description, int numOfExtrasystole){
        String json = fromArrayToJson(dataECG);
        databaseDimension = new DatabaseDimension(
                date, time, json, pulse, description, numOfExtrasystole);
        databaseDimension.save();
    }

    public static DatabaseDimension loadData(long id){
        databaseDimension = DatabaseDimension.findById(DatabaseDimension.class,id);
        return databaseDimension;
    }

    public static String fromArrayToJson(ArrayList<Integer> dataECG){
        return new Gson().toJson(dataECG);
    }

    public static ArrayList<Float> fromJsonToArray(String data){
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

//    public static ArrayList<ArrayList<DatabaseDimension>> getPreparedData(){
//        List<DatabaseDimension> dbList = getList();
//        ArrayList<ArrayList<DatabaseDimension>> sendData = new ArrayList<>();
//        String startDate;
//        boolean state;
//        Iterator<DatabaseDimension> i = dbList.iterator();
//        while (!dbList.isEmpty()){
//            ArrayList<DatabaseDimension> list = new ArrayList<>();
//            startDate = dbList.get(0).getDate();
//            state = true;
//            while (i.hasNext() && state) {
//                DatabaseDimension innerData = i.next();
//                if (startDate.equals(innerData.getDate())) {
//                    list.add(innerData);
//                    i.remove();
//                }
//                else state = false;
//            }
//            if (!list.isEmpty()) sendData.add(list);
//            i = dbList.iterator();
//        }
//        return sendData;
//    }

    public static List<DatabaseDimension> getPreparedData(){
        List<DatabaseDimension> dbList = getList();
        Collections.reverse(dbList);
        return dbList;
    }
}

