package by.bsuir.health.bean;

import java.util.ArrayList;
import java.util.Collections;

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

    public SignalAnalysis(){
        this.data = new ArrayList<>();
        this.listOfIndex = new ArrayList<>();
    }

    public void setData(ArrayList<Integer> data, String mode) {
        this.data = data;
        this.mode = mode;
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

    private boolean analyseSpo() {
        //TODO
        return false;
    }
}

