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

    private final ArrayList<SdFile> images;
    private final LayoutInflater inflater;
    private final int iconType;

    public ListImages(Context context, ArrayList<SdFile> images, int iconType) {
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

        SdFile image = images.get(position);
        if (image != null) {
            ((TextView) view.findViewById(R.id.tv_name)).setText(image.toString());
            ((TextView) view.findViewById(R.id.tv_address)).setText(image.toString());
            ((ImageView) view.findViewById(R.id.iv_icon)).setImageResource(iconType);
        }
        return view;
    }
}
