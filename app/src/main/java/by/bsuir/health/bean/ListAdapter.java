package by.bsuir.health.bean;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import by.bsuir.health.R;

/**
 * @author Pablo on 10.12.2020
 * @project Health
 */

public class ListAdapter extends BaseAdapter {

    private static final int RESOURCE_LAYOUT = R.layout.list_item;

    private final ArrayList<BluetoothDevice> bluetoothDevices;
    private final LayoutInflater inflater;
    private final int iconType;

    public ListAdapter(Context context, ArrayList<BluetoothDevice> bluetoothDevices, int iconType) {
        this.bluetoothDevices = bluetoothDevices;
        this.iconType = iconType;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return bluetoothDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(RESOURCE_LAYOUT, parent, false);

        BluetoothDevice device = bluetoothDevices.get(position);
        if (device != null) {
            ((TextView) view.findViewById(R.id.tv_name)).setText(device.getName());
            ((TextView) view.findViewById(R.id.tv_address)).setText(device.getAddress());
            ((ImageView) view.findViewById(R.id.iv_icon)).setImageResource(iconType);
        }
        return view;
    }
}
