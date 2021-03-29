package by.bsuir.health.dao;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Pablo on 24.03.2021
 * @project Health
 */
public class DBHelper {
    private static DataDB dateDB;
    public DBHelper(){}

    public static void SaveToDB(String date,String time, ArrayList<Integer> dataECG, int pulse, int
            description, int numOfExtrasystole){
        String json = fromArrayToJson(dataECG);
        dateDB = new DataDB(date,time,json,pulse, description, numOfExtrasystole);
        dateDB.save();
    }

    public static DataDB loadData(long id){
        dateDB = DataDB.findById(DataDB.class,id);
        return dateDB;
    }

    public static String fromArrayToJson(ArrayList<Integer> dataECG){
        return new Gson().toJson(dataECG);
    }

    public static ArrayList<Float> fromJsonToArray(String data){
        Type type = new TypeToken<ArrayList<Integer>>(){}.getType();
        return new Gson().fromJson(data,type);
    }

    public static void deleteAll(){
        DataDB.deleteAll(DataDB.class);
    }

    public static void deleteItem(long id){
        dateDB = DataDB.findById(DataDB.class,id);
        dateDB.delete();
    }

    public static List<DataDB> getList(){
        return DataDB.listAll(DataDB.class);
    }

    public static ArrayList<ArrayList<DataDB>> getPreparedData(){
        List<DataDB> dbList = getList();
        ArrayList<ArrayList<DataDB>> sendData = new ArrayList<>();
        String startDate;
        boolean state;
        Iterator<DataDB> i = dbList.iterator();
        while (!dbList.isEmpty()){
            ArrayList<DataDB> list = new ArrayList<DataDB>();
            startDate = dbList.get(0).getDate();
            state = true;
            while (i.hasNext() && state) {
                DataDB innerData = i.next();
                if (startDate.equals(innerData.getDate())) {
                    list.add(innerData);
                    i.remove();
                }
                else state = false;
            }
            if (!list.isEmpty()) sendData.add(list);
            i = dbList.iterator();
        }
        return sendData;
    }
}

