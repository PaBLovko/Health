package by.bsuir.health.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import by.bsuir.health.bean.BluetoothConnector;
import by.bsuir.health.bean.Pulse;
import by.bsuir.health.controller.ThreadController;
import by.bsuir.health.controller.ViewController;
import by.bsuir.health.dao.DatabaseHelper;
import by.bsuir.health.exeption.bluetooth.BluetoothException;
import by.bsuir.health.ui.ViewActivity;

/**
 * @author Pablo on 08.03.2021
 * @project Health
 */
public class ClickService implements View.OnClickListener{

    private ViewActivity viewActivity;
    private Pulse pulse;
    private Context context;

    public ClickService(ViewActivity viewActivity, Pulse pulse, Context context) {
        this.viewActivity = viewActivity;
        this.pulse = pulse;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(viewActivity.getBtnEnableSearch())) {
            try {
                BluetoothConnector.enableSearch();
            } catch (BluetoothException e) {
                e.printStackTrace();
            }
        } else if (v.equals(viewActivity.getBtnDisconnect())) {
            try {
                new ThreadController().disconnection(pulse);
            } catch (BluetoothException | IOException e) {
                e.printStackTrace();
            }
            viewActivity.showFrameControllers();
        } else if (v.equals(viewActivity.getBtnStart())){
            //TODO START TIMER (1 MIN)
            pulse.startTimer();
            pulse.counter();
        } else if (v.equals(viewActivity.getBtnSave())){
            if (pulse.getSignalAnalysis().isAnalyzed()){
                Date date = new Date();
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat formatForDate = new SimpleDateFormat("dd.MM.yyyy");
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat formatForTime = new SimpleDateFormat("HH:mm E");
                DatabaseHelper.SaveToDB(formatForDate.format(date),formatForTime.format(date),
                        pulse.getChart().getData(), pulse.getSignalAnalysis().getMode(),
                        pulse.getSignalAnalysis().getPulse(),
                        pulse.getSignalAnalysis().getNumOfExtrasystoleInRow());
                new ViewController().viewToastShow(viewActivity.getActivity(),
                        "The analysis is saved");
            }else new ViewController().viewToastShow(viewActivity.getActivity(),
                    "The analysis was not carried out");
        }
    }
}
