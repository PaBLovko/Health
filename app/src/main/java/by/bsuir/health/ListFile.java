package by.bsuir.health;

import android.content.Context;
import android.graphics.Bitmap;
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

public class ListFile extends BaseAdapter {

    private static final int RESOURCE_LAYOUT = R.layout.list_item;

    private final ArrayList<SdFile> sdFiles;
    private final LayoutInflater inflater;

    public ListFile(Context context, ArrayList<SdFile> sdFiles) {
        this.sdFiles = sdFiles;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return sdFiles.size();
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

        SdFile file = sdFiles.get(position);
        if (file != null) {
            ((TextView) view.findViewById(R.id.tv_name)).setText(file.getName());
            ((TextView) view.findViewById(R.id.tv_address)).setText(file.getDescription());
            ((ImageView) view.findViewById(R.id.iv_icon)).setImageBitmap(file.getImage());
        }
        return view;
    }
}
