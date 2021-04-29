package by.bsuir.health.bean;

import biz.source_code.dsp.filter.FilterCharacteristicsType;
import biz.source_code.dsp.filter.FilterPassType;
import biz.source_code.dsp.filter.IirFilter;
import biz.source_code.dsp.filter.IirFilterDesignFisher;

/**
 * @author Pablo on 27.04.2021
 * @project Health
 */
public class Filter {
    private IirFilter iirFilterButt;
    private boolean select;
    public Filter(boolean select) {
        iirFilterButt = new IirFilter(IirFilterDesignFisher.design(FilterPassType.lowpass,
                FilterCharacteristicsType.butterworth, 6, 0, 0.1, 0.49));
        this.select = select;
    }

    public int step(double data){
        if (select) return IirBut(data);
        else return (int)data;
    }

    private int IirBut(double data){
        return (int) iirFilterButt.step(data);
    }
}
