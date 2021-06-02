package by.bsuir.kazhamiakin.service;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import by.bsuir.kazhamiakin.controller.ViewController;
import by.bsuir.kazhamiakin.dao.DatabaseDimension;
import by.bsuir.kazhamiakin.dao.DatabaseHelper;
import by.bsuir.kazhamiakin.ui.ViewActivity;

/**
 * @author Pablo on 29.04.2021
 * @project Health
 */
public class ItemLongClickListener implements AdapterView.OnItemLongClickListener {

    private final ViewActivity viewActivity;
    private List<DatabaseDimension> list;

    public ItemLongClickListener(ViewActivity viewActivity) {
        this.viewActivity = viewActivity;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.equals(viewActivity.getListImages())) {
            createDialogDelete(id);
            return true;
        }
        return false;
    }

    public void createDialogDelete(final long id) {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(viewActivity.getActivity());
        quitDialog.setTitle("Хотите удалить запись?");

        quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                list = DatabaseHelper.getPreparedData();
                DatabaseHelper.deleteItem(list.get((int)id).getId());
                new ViewController().showStorage(viewActivity);
                new ViewController().viewToastShow(viewActivity.getActivity(),
                        "Deletion completed");
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

}
