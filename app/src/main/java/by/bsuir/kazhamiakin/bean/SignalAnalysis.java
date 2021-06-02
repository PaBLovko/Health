package by.bsuir.kazhamiakin.bean;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IntSummaryStatistics;


/**
 * @author Pablo on 24.03.2021
 * @project Health
 */
public class SignalAnalysis {
    private ArrayList<Integer> data;
    private ArrayList<Integer> listOfIndex;
    private int numOfExtrasystole;
    private int numOfExtrasystoleInRow;
    private int pulse;
    private int description;
    private boolean isAnalyzed;
    private String mode;
    private int spo;
    private int measurementTime;

    public SignalAnalysis(){
        this.data = new ArrayList<>();
        this.listOfIndex = new ArrayList<>();
    }

    public void setData(ArrayList<Integer> data, ArrayList<Integer> listOfIndex,
                        String mode, int measurementTime) {
        this.listOfIndex = listOfIndex;
        this.data = data;
        this.mode = mode;
        this.measurementTime = measurementTime;
    }

    public int getPulse(){
        return pulse;
    }
    public int getNumOfExtrasystole() {
        return numOfExtrasystole;
    }
    public int getNumOfExtrasystoleInRow() {
        return numOfExtrasystoleInRow;
    }
    public int getDescription() {
        return description;
    }

    public String getMode() {
        return mode;
    }

    public int getSpo() {
        return spo;
    }

    public void setDescription(int description) {
        this.description = description;
    }

    public boolean isAnalyzed() {
        return isAnalyzed;
    }

    private void analysePulse(){
        float maxVal = Collections.max(data);
        float percentOfMaxVal = maxVal - (maxVal*30)/100;
        pulse = 0;
        for(int i = 0; i< data.size()-2; i++){
            if((data.get(i+1)- data.get(i))*(data.get(i+2)- data.get(i+1))<=0&& data
                    .get(i+1)>=percentOfMaxVal){
                pulse++;
                listOfIndex.add(i+1);
            }
        }
        pulse=pulse*60*1000/measurementTime;
    }

    private boolean analyseExtrasystole(){
        int len = listOfIndex.size()/3;
        int tempOfExtrasystoleInRow = 0;
        numOfExtrasystoleInRow = 0;
        numOfExtrasystole = 0;
        int average = getAverageSize(len);
        for (int i = 1;i<listOfIndex.size()-1;i++) {
            int valueFirst = listOfIndex.get(i);
            int valueSecond = listOfIndex.get(i+1);
            int btw = valueSecond-valueFirst;
            if(btw>average+10||btw<average-10){
                numOfExtrasystole++;
                tempOfExtrasystoleInRow++;
            }
            else{
                if(tempOfExtrasystoleInRow>numOfExtrasystoleInRow)
                    numOfExtrasystoleInRow = tempOfExtrasystoleInRow;
                tempOfExtrasystoleInRow = 0;
            }
        }
        return numOfExtrasystoleInRow<2;
    }

    private int getAverageSize(int len){
        int average = 0;
        for (int i = 0;i<len;i++) {
            int val = listOfIndex.get(i);
            average+=val;
        }
        len++;
        return average/len;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public int analyseData(){
        if (!mode.equals("spo")){
            listOfIndex.clear();
        }
        analysePulse();
        boolean state;
        if (mode.equals("ecg")){
             state = analyseExtrasystole();
        }else  state = analyseSpo();
        description = 0;
        if (pulse < 50)
            description = 1;
        else if (pulse > 120)
            description = 2;
        if (!state) description = 3;
        isAnalyzed = true;
        return description;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean analyseSpo() {
        pulse = (int) listOfIndex.stream().filter(x -> x==1).count()*60*1000/measurementTime;
        IntSummaryStatistics stats = data
                .stream()
                .mapToInt(Integer::intValue)
                .summaryStatistics();
        spo = (int) stats.getAverage();
        return spo >= 95;
    }
}

