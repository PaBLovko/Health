package by.bsuir.health.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import by.bsuir.health.MainActivity;
import by.bsuir.health.R;
import by.bsuir.health.bean.BluetoothConnector;
import by.bsuir.health.dao.DatabaseDimension;
import by.bsuir.health.dao.DatabaseHelper;
import by.bsuir.health.exeption.bluetooth.BluetoothException;
import by.bsuir.health.service.CheckedChangeService;
import by.bsuir.health.service.ClickService;
import by.bsuir.health.service.ItemClickService;
import by.bsuir.health.service.ItemLongClickListener;
import by.bsuir.health.ui.ListAdapter;
import by.bsuir.health.ui.ListDimensions;
import by.bsuir.health.ui.ViewActivity;

import static by.bsuir.health.ui.ViewActivity.BT_BOUNDED;
import static by.bsuir.health.ui.ViewActivity.BT_SEARCH;

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

    public ListDimensions getListDimension(Context context,
                                           List<DatabaseDimension> databaseDimensions){
        List<String> textDescription = getTextDescription(context, databaseDimensions);
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
        return new ListDimensions(context, databaseDimensions, textDescription);
    }

    public List<String> getTextDescription(Context context,
                                           List<DatabaseDimension> databaseDimensions){
        List<String> textDescription = new ArrayList<>();
        for (DatabaseDimension databaseDimension : databaseDimensions){
            switch (databaseDimension.getDescription()) {
                case 0:
                    textDescription.add(context.getString(R.string.done));
                    break;
                case 1:
                case 2:
                    textDescription.add(context.getString(R.string.warning));
                    break;
                default:
                    textDescription.add(context.getString(R.string.error));
                     break;
            }
        }
        return textDescription;
    }

    public void setProgressDialog(Context context, ProgressDialog progressDialog){
        progressDialog.setCancelable(false);
        progressDialog.setTitle(context.getString(R.string.connecting));
        progressDialog.setMessage(context.getString(R.string.please_wait));
    }
}
