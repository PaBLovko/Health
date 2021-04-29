package by.bsuir.health.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import by.bsuir.health.R;
import by.bsuir.health.dao.DatabaseDimension;

/**
 * @author Pablo on 10.12.2020
 * @project Health
 */

public class ListDimensions extends BaseAdapter {

    private static final int RESOURCE_LAYOUT = R.layout.list_item;

    private final List<DatabaseDimension> databaseDimensions;
    private final List<String> textDimensions;
    private final LayoutInflater inflater;

    public ListDimensions(Context context, List<DatabaseDimension> databaseDimensions,
                          List<String> textDimensions) {
        this.databaseDimensions = databaseDimensions;
        this.textDimensions = textDimensions;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return databaseDimensions.size();
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
        DatabaseDimension dimension = databaseDimensions.get(position);
        String textDimension = textDimensions.get(position);
        if (dimension != null) {
            String data = dimension.getTime()+" "+dimension.getDate();
            int iconType = dimension.getDescription();
            ((TextView) view.findViewById(R.id.tv_name)).setText(data);
            ((TextView) view.findViewById(R.id.tv_address)).setText(textDimension);
            ((ImageView) view.findViewById(R.id.iv_icon)).setImageResource(iconType);
        }
        return view;
    }
}
