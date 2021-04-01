package by.bsuir.health.dao;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * @author Pablo on 24.03.2021
 * @project Health
 */
public class DatabaseDimension extends SugarRecord {
    @Unique
    private String dataECG;
    private String date;
    private String time;
    private int pulse;
    private int description;
    private int numOfExtrasystole;

    public DatabaseDimension(){}

    public DatabaseDimension(String date, String time, String dataECG,  int pulse, int description,
                             int numOfExtrasystole){
        super();
        this.date = date;
        this.time = time;
        this.dataECG = dataECG;
        this.description = description;
        this.numOfExtrasystole = numOfExtrasystole;
        this.pulse = pulse;
    }
    public void setDate(String date){
        this.date = date;
    }
    public String getDate(){
        return this.date;
    }
    public String getDataECG() {
        return dataECG;
    }
    public void setDataECG(String dataECG) {
        this.dataECG = dataECG;
    }
    public int getPulse() {
        return pulse;
    }
    public void setPulse(int pulse) {
        this.pulse = pulse;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public int getNumOfExtrasystole() {
        return numOfExtrasystole;
    }
    public void setNumOfExtrasystole(int numOfExtrasystole) {
        this.numOfExtrasystole = numOfExtrasystole;
    }
    public int getDescription() {
        return description;
    }
    public void setDescription(int description) {
        this.description = description;
    }
}

