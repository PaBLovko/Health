package by.bsuir.health.bean;

import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

/**
 * @author Pablo on 24.03.2021
 * @project Health
 */
public class SignalAnalysis {
    private ArrayList<Integer> ecgData;
    private ArrayList<Integer> listOfIndex;
    private int numOfExtrasystole;
    private int numOfExtrasystoleInRow;
    private int pulse;

    @Inject
    public SignalAnalysis(){}

    public void setDataECG(ArrayList<Integer> ecgData){
        this.ecgData = ecgData;
        listOfIndex = new ArrayList<>();
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

    private void analysePulse(){
        float maxVal = Collections.max(ecgData);
        float percentOfMaxVal = maxVal - (maxVal*30)/100;
        pulse = 0;
        for(int i = 0;i<ecgData.size()-2;i++){
            if((ecgData.get(i+1)-ecgData.get(i))*(ecgData.get(i+2)-ecgData.get(i+1))<=0&&ecgData
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
        int mode = 0;
        if (pulse < 55) mode = 1;
        else if (pulse > 90) mode = 2;
        if (!state) mode = 3;
        return mode;
    }

}

