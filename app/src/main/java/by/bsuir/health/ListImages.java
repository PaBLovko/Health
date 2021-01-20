package by.bsuir.health;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author Pablo on 10.12.2020
 * @project Health
 */

public class ListImages extends BaseAdapter {

    private static final int RESOURCE_LAYOUT = R.layout.list_item;

    private final ArrayList<Image> images;
    private final LayoutInflater inflater;
    private final int iconType;

    public ListImages(Context context, ArrayList<Image> images, int iconType) {
        this.images = images;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.iconType = iconType;
    }

    @Override
    public int getCount() {
        return images.size();
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

        Image device = images.get(position);
        if (device != null) {
            ((TextView) view.findViewById(R.id.tv_name)).setText(device.toString());
            ((TextView) view.findViewById(R.id.tv_address)).setText(device.toString());
            ((ImageView) view.findViewById(R.id.iv_icon)).setImageResource(iconType);
        }
        return view;
    }
}
