package by.bsuir.health.service;

import android.content.Context;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import by.bsuir.health.bean.BluetoothConnector;
import by.bsuir.health.ui.ListFile;
import by.bsuir.health.bean.SdFile;
import by.bsuir.health.bean.Pulse;
import by.bsuir.health.controller.SdFileController;
import by.bsuir.health.controller.ThreadController;
import by.bsuir.health.exeption.bluetooth.BluetoothException;
import by.bsuir.health.ui.ViewActivity;

/**
 * @author Pablo on 08.03.2021
 * @project Health
 */
public class ClickService implements View.OnClickListener{

    private static final List<OnItemChangedListener> listeners = new ArrayList<>();

    private ViewActivity viewActivity;
    private Pulse pulse;
    private ArrayList<SdFile> sdFiles;
    private String storageDirectory;
    private Context context;

    public ClickService(ViewActivity viewActivity, Pulse pulse, Context context,
                        ArrayList<SdFile> sdFiles, String storageDirectory) {
        this.viewActivity = viewActivity;
        this.pulse = pulse;
        this.sdFiles = sdFiles;
        this.storageDirectory = storageDirectory;
        this.context = context;
    }

    public void setPulse(Pulse pulse) {
        this.pulse = pulse;
    }

    public ArrayList<SdFile> getSdFiles() {
        return sdFiles;
    }

    public static void addItemListener(OnItemChangedListener l) {
        listeners.add(l);
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
            viewActivity.showFrameControls();
        } else if (v.equals(viewActivity.getBtnStart())){
            //TODO START TIMER (1 MIN)
            pulse.startTimer();
            pulse.counter();
        } else if (v.equals(viewActivity.getBtnStorage())){
            try {
                new ThreadController().disconnection(pulse);
            } catch (BluetoothException | IOException e) {
                e.printStackTrace();
            }
            sdFiles = new SdFileController().getListFile(storageDirectory);
            viewActivity.setListImages(new ListFile(context, sdFiles));
            viewActivity.showFrameStorage();
            for (OnItemChangedListener l : listeners)
                l.OnItemChanged();
        }
    }
}
