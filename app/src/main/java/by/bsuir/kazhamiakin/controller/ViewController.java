package by.bsuir.kazhamiakin.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import by.bsuir.kazhamiakin.MainActivity;
import by.bsuir.kazhamiakin.R;
import by.bsuir.kazhamiakin.bean.BluetoothConnector;
import by.bsuir.kazhamiakin.bean.SignalAnalysis;
import by.bsuir.kazhamiakin.dao.DatabaseDimension;
import by.bsuir.kazhamiakin.dao.DatabaseHelper;
import by.bsuir.kazhamiakin.exeption.bluetooth.BluetoothException;
import by.bsuir.kazhamiakin.service.CheckedChangeService;
import by.bsuir.kazhamiakin.service.ClickService;
import by.bsuir.kazhamiakin.service.ItemClickService;
import by.bsuir.kazhamiakin.service.ItemLongClickListener;
import by.bsuir.kazhamiakin.ui.ListAdapter;
import by.bsuir.kazhamiakin.ui.ListDimensions;
import by.bsuir.kazhamiakin.ui.ViewActivity;

import static by.bsuir.kazhamiakin.ui.ViewActivity.BT_BOUNDED;
import static by.bsuir.kazhamiakin.ui.ViewActivity.BT_SEARCH;

/**
 * @author Pablo on 08.03.2021
 * @project Health
 */
public class ViewController {
    public void viewWarning(final Activity activity, final ViewActivity viewActivity,
                            final String message){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewActivity.getProgressDialog().dismiss();
                Toast.makeText(activity, message,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setEtConsole(final ViewActivity viewActivity, final SignalAnalysis signalAnalysis){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                setResultInView(viewActivity, signalAnalysis);
            }
        });
    }

    public void viewToastShow(Activity activity, String text){
        Toast.makeText(activity,text, Toast.LENGTH_SHORT).show();
    }

    public void setListeners(ViewActivity viewActivity, CheckedChangeService checkedChangeService,
                             ItemClickService itemClickService, ClickService clickService,
                             ItemLongClickListener itemLongClickListener) {
        viewActivity.getSwitchEnableBt().setOnCheckedChangeListener(checkedChangeService);
        viewActivity.getBtnEnableSearch().setOnClickListener(clickService);
        viewActivity.getListDevices().setOnItemClickListener(itemClickService);
        viewActivity.getBtnSave().setOnClickListener(clickService);
        viewActivity.getListImages().setOnItemClickListener(itemClickService);
        viewActivity.getBtnStart().setOnClickListener(clickService);
        viewActivity.getBtnDisconnect().setOnClickListener(clickService);
        viewActivity.getSwitchLed().setOnCheckedChangeListener(checkedChangeService);
        viewActivity.getSwitchBuzzer().setOnCheckedChangeListener(checkedChangeService);
        viewActivity.getListImages().setOnItemLongClickListener(itemLongClickListener);
    }

    public void showStorage(ViewActivity viewActivity){
        ListDimensions listDimensions = new ViewController().getListDimension(
                viewActivity.getActivity(), DatabaseHelper.getPreparedData());
        viewActivity.setListImages(listDimensions);
        viewActivity.showFrameStorage();
    }

    public void openQuitDialog(final MainActivity mainActivity) {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(mainActivity);
        quitDialog.setTitle("Выход: Вы уверены?");

        quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                mainActivity.finish();
            }
        });

        quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });
        quitDialog.show();
    }



    public ListAdapter getListAdapter(Context context, BluetoothConnector bluetoothConnector,
                                      int type) throws BluetoothException {
        bluetoothConnector.clear();
        int iconType = R.drawable.ic_bluetooth_bounded_device;
        switch (type) {
            case BT_BOUNDED:
                bluetoothConnector.setBluetoothDevices(BluetoothConnector.getBondedDevices());
                iconType = R.drawable.ic_bluetooth_bounded_device;
                break;
            case BT_SEARCH:
                iconType = R.drawable.ic_bluetooth_search_device;
                break;
        }
        return new ListAdapter(context, bluetoothConnector.getBluetoothDevices(), iconType);
    }

    public ListDimensions getListDimension(Activity activity,
                                           List<DatabaseDimension> databaseDimensions){
        List<String> textDescription = getTextOfArrayDescription(activity, databaseDimensions);
        for (DatabaseDimension databaseDimension : databaseDimensions){
            switch (databaseDimension.getDescription()) {
                case 0:
                    databaseDimension.setDescription(R.drawable.ic_description_done);
                    break;
                case 1:
                case 2:
                    databaseDimension.setDescription(R.drawable.ic_description_warning);
                    break;
                default:
                    databaseDimension.setDescription(R.drawable.ic_description_error);
                    break;
            }
        }
        return new ListDimensions(activity, databaseDimensions, textDescription);
    }

    public List<String> getTextOfArrayDescription(Activity activity,
                                                  List<DatabaseDimension> databaseDimensions){
        List<String> textDescription = new ArrayList<>();
        for (DatabaseDimension databaseDimension : databaseDimensions){
            textDescription.add(getTextDescription(activity, databaseDimension.getDescription()));
        }
        return textDescription;
    }

    public String getTextDescription(Activity activity, int description){
        switch (description) {
            case 0:
                return activity.getString(R.string.done);
            case 1:
            case 2:
                return activity.getString(R.string.warning);
            default:
                return activity.getString(R.string.error);
        }
    }

    public void setResultInView(ViewActivity viewActivity, SignalAnalysis signalAnalysis){
        String text;
        if (signalAnalysis.getMode().equals("ecg")){
            text = getTextDescription(
                    viewActivity.getActivity(), signalAnalysis.getDescription()) + "\npulse: " +
                    signalAnalysis.getPulse() + "\nextrasystole: " +
                    signalAnalysis.getNumOfExtrasystoleInRow();
        } else {
            text = getTextDescription(
                    viewActivity.getActivity(), signalAnalysis.getDescription()) + "\npulse: " +
                    signalAnalysis.getPulse() + "\nspo: " + signalAnalysis.getSpo();
        }
        viewActivity.setEtConsoleAndMovementMethod(text, new ScrollingMovementMethod());
    }

    public String translate(String mode){
        if (mode.equals("Пульсометр") || mode.equals("Кардиомонитор"))
            return "ecg";
        else return "spo";
    }

    public void setProgressDialog(Context context, ProgressDialog progressDialog){
        progressDialog.setCancelable(false);
        progressDialog.setTitle(context.getString(R.string.connecting));
        progressDialog.setMessage(context.getString(R.string.please_wait));
    }
}
