package by.bsuir.health.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import by.bsuir.health.MainActivity;
import by.bsuir.health.R;
import by.bsuir.health.service.CheckedChangeService;
import by.bsuir.health.service.ClickService;
import by.bsuir.health.service.ItemClickService;
import by.bsuir.health.ui.ViewActivity;

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

    public void vieLoading(final Activity activity, final ViewActivity viewActivity){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewActivity.getProgressDialog().show();
            }
        });
    }

    public void setListeners(ViewActivity viewActivity, CheckedChangeService checkedChangeService,
                             ItemClickService itemClickService, ClickService clickService) {
        viewActivity.getSwitchEnableBt().setOnCheckedChangeListener(checkedChangeService);
        viewActivity.getBtnEnableSearch().setOnClickListener(clickService);
        viewActivity.getListDevices().setOnItemClickListener(itemClickService);
        viewActivity.getBtnStorage().setOnClickListener(clickService);
        viewActivity.getListImages().setOnItemClickListener(itemClickService);
        viewActivity.getBtnStart().setOnClickListener(clickService);
        viewActivity.getBtnDisconnect().setOnClickListener(clickService);
        viewActivity.getSwitchLed().setOnCheckedChangeListener(checkedChangeService);
        viewActivity.getSwitchBuzzer().setOnCheckedChangeListener(checkedChangeService);
    }

    public void openQuitDialog(final MainActivity mainActivity) {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(mainActivity);
        quitDialog.setTitle("Выход: Вы уверены?");

        quitDialog.setPositiveButton("Таки да!", new DialogInterface.OnClickListener() {
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

    public void setProgressDialog(Context context, ProgressDialog progressDialog){
        progressDialog.setCancelable(false);
        progressDialog.setTitle(context.getString(R.string.connecting));
        progressDialog.setMessage(context.getString(R.string.please_wait));
    }
}
