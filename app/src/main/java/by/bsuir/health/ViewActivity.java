package by.bsuir.health;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.text.method.MovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * @author Pablo on 30.01.2021
 * @project Health
 */
public class ViewActivity extends View {
    private final FrameLayout frameMessage;
    private final LinearLayout frameControls;
    private final RelativeLayout frameLedControls;
    private final LinearLayout frameStorage;

    private ListView       listImages;

    private Button         btnDisconnect;
    private Button         btnStart;
    private Button         btnStorage;

    private Switch         switchBuzzer;
    private Switch         switchLed;

    private EditText       etConsole;

    private Switch         switchEnableBt;

    private Button         btnEnableSearch;

    private ProgressBar    pbProgress;

    private ListView       listDevices;

    private GraphView      gvGraph;

    private LineGraphSeries seriesTemp;
    private LineGraphSeries seriesRand;

    private ProgressDialog      progressDialog;

    public ViewActivity(AppCompatActivity appCompatActivity) {
        super(appCompatActivity);
        frameMessage        = appCompatActivity.findViewById(R.id.frame_message);
        frameControls       = appCompatActivity.findViewById(R.id.frame_control);
        frameStorage        = appCompatActivity.findViewById(R.id.frame_storage);
        frameLedControls    = appCompatActivity.findViewById(R.id.frameLedControls);
        listImages          = findViewById(R.id.lv_image);

        switchEnableBt      = findViewById(R.id.switch_enable_bt);
        btnEnableSearch     = findViewById(R.id.btn_enable_search);
        pbProgress          = findViewById(R.id.pb_progress);
        listDevices         = findViewById(R.id.lv_device);

        btnDisconnect       = findViewById(R.id.btn_disconnect);
        btnStart            = findViewById(R.id.btn_start);
        btnStorage          = findViewById(R.id.btn_storage);
        switchBuzzer        = findViewById(R.id.switch_buzzer);
        switchLed           = findViewById(R.id.switch_led);
        etConsole           = findViewById(R.id.et_console);

        gvGraph   = findViewById(R.id.gv_graph);

        seriesTemp          = new LineGraphSeries();
        seriesRand          = new LineGraphSeries();
        seriesTemp.setColor(Color.GREEN);
        seriesRand.setColor(Color.RED);

//        progressDialog = new ProgressDialog(context);
//        progressDialog.setCancelable(false);
//        progressDialog.setTitle(getString(R.string.connecting));
//        progressDialog.setMessage(getString(R.string.please_wait));
    }

    public void showFrameMessage() {
        frameMessage.setVisibility(View.VISIBLE);
        frameLedControls.setVisibility(View.GONE);
        frameControls.setVisibility(View.GONE);
        frameStorage.setVisibility(View.GONE);
    }

    public void showFrameControls() {
        frameMessage.setVisibility(View.GONE);
        frameLedControls.setVisibility(View.GONE);
        frameControls.setVisibility(View.VISIBLE);
        frameStorage.setVisibility(View.GONE);
    }

    public void showFrameLedControls() {
        frameLedControls.setVisibility(View.VISIBLE);
        frameMessage.setVisibility(View.GONE);
        frameControls.setVisibility(View.GONE);
        frameStorage.setVisibility(View.GONE);
    }

    public void showFrameStorage(){
        frameLedControls.setVisibility(View.GONE);
        frameMessage.setVisibility(View.GONE);
        frameControls.setVisibility(View.GONE);
        frameStorage.setVisibility(View.VISIBLE);
    }

    public void setGvGraph(int MaxX) {
        this.gvGraph.addSeries(seriesTemp);
        this.gvGraph.addSeries(seriesRand);
        this.gvGraph.getViewport().setMinX(0);
        this.gvGraph.getViewport().setMaxX(MaxX);
        this.gvGraph.getViewport().setXAxisBoundsManual(true);

    }

    public GraphView getGvGraph() {
        return gvGraph;
    }


    public void setSwitchEnableBtChecked(boolean switchEnableBt) {
        this.switchEnableBt.setChecked(switchEnableBt);
    }

    public Switch getSwitchEnableBt() {
        return switchEnableBt;
    }

    public Button getBtnDisconnect() {
        return btnDisconnect;
    }

    public Button getBtnStart() {
        return btnStart;
    }

    public Button getBtnStorage() {
        return btnStorage;
    }

    public ListView getListImages() {
        return listImages;
    }

    public void setListImages(ListFile listFile) {
        this.listImages.setAdapter(listFile);
    }

    public ListView getListDevices() {
        return listDevices;
    }

    public void setBtnEnableSearchStart() {
        this.btnEnableSearch.setText(R.string.start_search);
    }

    public void setBtnEnableSearchStop() {
        this.btnEnableSearch.setText(R.string.stop_search);
    }

    public Button getBtnEnableSearch() {
        return btnEnableSearch;
    }

    public void setPbProgressNoVisibility() {
        this.pbProgress.setVisibility(View.GONE);
    }

    public void setPbProgressVisibility() {
        this.pbProgress.setVisibility(View.VISIBLE);
    }

    public Switch getSwitchBuzzer() {
        return switchBuzzer;
    }

    public Switch getSwitchLed() {
        return switchLed;
    }

    public void setListDevices(ListAdapter listAdapter) {
        this.listDevices.setAdapter(listAdapter);
    }

    public void setEtConsoleAndMovementMethod(String text, MovementMethod movementMethod) {
        this.etConsole.setText(text);
        this.etConsole.setMovementMethod(movementMethod);
    }

    public LineGraphSeries getSeriesTemp() {
        return seriesTemp;
    }

    public LineGraphSeries getSeriesRand() {
        return seriesRand;
    }

}

