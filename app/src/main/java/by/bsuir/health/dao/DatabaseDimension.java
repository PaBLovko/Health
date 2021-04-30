package by.bsuir.health.dao;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * @author Pablo on 24.03.2021
 * @project Health
 */
public class DatabaseDimension extends SugarRecord {
    @Unique
    private String data;
    private String date;
    private String time;
    private int pulse;
    private int description;
    private int numOfExtrasystole;
    private int spo;
    private String mode;

    public DatabaseDimension(){}

    public DatabaseDimension(String date, String time, String data,  int pulse, int description,
                             int numOfExtrasystole, int spo, String mode){
        super();
        this.date = date;
        this.time = time;
        this.data = data;
        this.description = description;
        this.numOfExtrasystole = numOfExtrasystole;
        this.pulse = pulse;
        this.spo = spo;
        this.mode = mode;
    }
    public void setDate(String date){
        this.date = date;
    }
    public String getDate(){
        return this.date;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
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
    public int getSpo() {
        return spo;
    }
    public void setSpo(int spo) {
        this.spo = spo;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}

