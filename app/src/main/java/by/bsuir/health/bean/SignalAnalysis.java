package by.bsuir.health.bean;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Pablo on 24.03.2021
 * @project Health
 */
public class SignalAnalysis {
    private ArrayList<Integer> Data;
    private ArrayList<Integer> listOfIndex;
    private int numOfExtrasystole;
    private int numOfExtrasystoleInRow;
    private int pulse;
    private int mode;
    private boolean isAnalyzed;

    public SignalAnalysis(){
        this.Data = new ArrayList<>();
        this.listOfIndex = new ArrayList<>();
    }

    public void setData(ArrayList<Integer> data) {
        Data = data;
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
    public int getMode() {
        return mode;
    }

    public boolean isAnalyzed() {
        return isAnalyzed;
    }

    private void analysePulse(){
        float maxVal = Collections.max(Data);
        float percentOfMaxVal = maxVal - (maxVal*30)/100;
        pulse = 0;
        for(int i = 0; i< Data.size()-2; i++){
            if((Data.get(i+1)- Data.get(i))*(Data.get(i+2)- Data.get(i+1))<=0&& Data
                    .get(i+1)>=percentOfMaxVal){
                pulse++;
                listOfIndex.add(i+1);
            }
        }
        pulse=pulse*2;
    }

    private boolean analyseExtrasystole(){
        int len = listOfIndex.size()/3;
        int tempOfExtrasystoleInRow = 0;
        numOfExtrasystoleInRow = 0;
        numOfExtrasystole = 0;
        int average = getAverageSize(len);
        for (int i = 1;i<listOfIndex.size()-1;i++) {
            int val1 = listOfIndex.get(i);
            int val2 = listOfIndex.get(i+1);
            int btw = val2-val1;
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

    public int analyseData(){
        analysePulse();
        boolean state = analyseExtrasystole();
        mode = 0;
        if (pulse < 50) mode = 1;
        else if (pulse > 120) mode = 2;
        if (!state) mode = 3;
        isAnalyzed = true;
        return mode;
    }

}

