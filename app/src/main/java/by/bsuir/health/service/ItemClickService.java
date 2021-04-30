package by.bsuir.health.service;

import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.AdapterView;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

import by.bsuir.health.bean.AsyncTaskConnect;
import by.bsuir.health.bean.BluetoothConnector;
import by.bsuir.health.bean.Pulse;
import by.bsuir.health.dao.DatabaseDimension;
import by.bsuir.health.dao.DatabaseHelper;
import by.bsuir.health.ui.ViewActivity;

/**
 * @author Pablo on 08.03.2021
 * @project Health
 */
public class ItemClickService implements AdapterView.OnItemClickListener {

    private final ViewActivity viewActivity;
    private final BluetoothConnector bluetoothConnector;
    private Pulse pulse;
    private AsyncTaskConnect asyncTaskConnect;

    public ItemClickService(ViewActivity viewActivity, Pulse pulse,
                            BluetoothConnector bluetoothConnector) {
        this.viewActivity = viewActivity;
        this.bluetoothConnector = bluetoothConnector;
        this.pulse = pulse;
        this.asyncTaskConnect = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.equals(viewActivity.getListDevices())) {
            BluetoothDevice device = bluetoothConnector.getBluetoothDevices().get(position);
            if (device != null) {
                asyncTaskConnect = new AsyncTaskConnect(device, bluetoothConnector,
                        pulse, viewActivity);
                asyncTaskConnect.execute();
            }
        }
        if (parent.equals(viewActivity.getListImages())){
            viewActivity.getGvResult().removeAllSeries();
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
            List<DatabaseDimension> databaseDimensionList = DatabaseHelper.getPreparedData();
            DatabaseDimension dimension =
                    DatabaseHelper.loadData(databaseDimensionList.get((int)id).getId());
            ArrayList<Integer> arrayDataList = DatabaseHelper.fromJsonToArray(dimension.getData());
            int index = 0;
            for (int data : arrayDataList){
                series.appendData(new DataPoint(index++, data), true,
                        arrayDataList.size());
            }
            viewActivity.getGvResult().addSeries(series);
            viewActivity.getGvResult().getViewport().setScalable(true);
            viewActivity.getGvResult().getViewport().setScalableY(true);
            viewActivity.getGvResult().getViewport().setScrollable(true);
            viewActivity.getGvResult().getViewport().setScrollableY(true);
            viewActivity.showFrameResult();
        }
    }
}
