package by.bsuir.health;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

/**
 * @author Pablo on 31.01.2021
 * @project Health
 */
public class ControllerActivity extends AppCompatActivity implements
        CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemClickListener,
        View.OnClickListener{

    private ViewActivity viewActivity;

    public ControllerActivity(AppCompatActivity appCompatActivity) {
        this.viewActivity = new ViewActivity(appCompatActivity);
    }

    public ViewActivity getViewActivity() {
        return viewActivity;
    }

    public void setViewActivity(ViewActivity viewActivity) {
        this.viewActivity = viewActivity;
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}
