package jp.co.akiguchilab.healthcaremanagement.geneticalgorithm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.akiguchilab.healthcaremanagement.R;

/**
 * Created by i09324 on 2014/08/28.
 */
public class GA extends Activity implements SensorEventListener, OnClickListener, Runnable {
    private static ProgressDialog waitDialog;

    private static int ACCELEROMETER_X = 0;
    private static int ACCELEROMETER_Y = 1;
    private static int ACCELEROMETER_Z = 2;

    private boolean SENSOR_ON_FLAG = false;

    private String filepath = "";
    private ArrayList<AccelerometerData> accelerometer = new ArrayList<AccelerometerData>();
    private Thread thread;
    private SensorManager manager;
    private GeneticAlgorithm GA = new GeneticAlgorithm();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.genetic_layout);

        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        LinearLayout layout = (LinearLayout) findViewById(R.id.genetic_linearLayout_afterRun);
        Button OnOff = (Button) findViewById(R.id.genetic_run_button);
        Button practice = (Button) findViewById(R.id.genetic_practice_button);
        Button complete = (Button) findViewById(R.id.genetic_complete_button);

        layout.setVisibility(View.INVISIBLE);
        OnOff.setOnClickListener(this);
        practice.setOnClickListener(this);
        complete.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        disableSensor();
    }

    @Override
    public void onClick(View v) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.genetic_linearLayout_afterRun);
        Button OnOff = (Button) findViewById(R.id.genetic_run_button);

        switch (v.getId()) {
            case R.id.genetic_run_button:
                if (!SENSOR_ON_FLAG) {
                    enableSensor();
                    layout.setVisibility(View.INVISIBLE);
                    OnOff.setText("停止");
                } else {
                    disableSensor();
                    OnOff.setText("やり直す");

                    showProgressDialog();
                }
                break;

            case R.id.genetic_practice_button:
                //TODO
                break;

            case R.id.genetic_complete_button:
                DialogFragment dialog = new confirmDialog();
                dialog.show(getFragmentManager(), "dialog");
                break;
        }
    }

    private void showProgressDialog() {
        waitDialog = new ProgressDialog(this);
        waitDialog.setMessage("設定中...");
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.show();

        thread = new Thread(this);
        thread.start();
    }

    public static class confirmDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("運動を登録しますか？")
                    .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            GA callingActivity = (GA) getActivity();
                            callingActivity.addDialog(true);

                            dismiss();
                        }
                    })
                    .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
            return builder.create();
        }
    }

    private void addDialog(boolean save) {
        if (save) {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            final View layout = inflater.inflate(R.layout.add_training_dialog, (ViewGroup) findViewById(R.id.add_training_dialog));
            final EditText text = (EditText) layout.findViewById(R.id.add_training_dialog_edittext);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(layout)
                    .setPositiveButton("登録", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String str = text.getText().toString();
                            addTraining(str, filepath);

                            setResult(RESULT_OK, new Intent());
                            finish();
                        }
                    })
                    .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
            builder.show();
        }
    }

    // 運動をcsvファイルに書き込み、追加する
    private void addTraining(String name, String path) {
        try {
            OutputStream out = openFileOutput("Training.csv", MODE_APPEND);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

            bw.newLine();
            bw.write(name + ",assets,danberu.png," + path);
            bw.flush();

            out.close();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableSensor() {
        List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            Sensor sensor = sensors.get(0);
            manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }
        SENSOR_ON_FLAG = true;
    }

    private void disableSensor() {
        manager.unregisterListener(this);
        SENSOR_ON_FLAG = false;
    }

    @Override
    public void run() {
        try {
            File sdcard_path = new File(Environment.getExternalStorageDirectory().getPath() + "/HealthCare");
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

            String Fs = File.separator;

            String filePath = sdcard_path + Fs + "activity_" + dateFormat.format(date);

            if (!sdcard_path.exists()) {
                sdcard_path.mkdir();
            }

            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(filePath, false), "UTF-8")
            );

            for (int i = 0; i < accelerometer.size(); i++) {
                bw.write(accelerometer.get(i).getAccelerometer_x() + ",");
                bw.write(accelerometer.get(i).getAccelerometer_y() + ",");
                bw.write(accelerometer.get(i).getAccelerometer_z() + "");
                bw.newLine();
            }
            bw.flush();
            bw.close();

            filePath = GA.start(accelerometer, getIntent().getIntExtra("linage", 0));
        } catch (IOException e) {
            e.printStackTrace();
        }

        waitDialog.dismiss();
        this.handler.sendEmptyMessage(0);
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.genetic_linearLayout_afterRun);
            layout.setVisibility(View.VISIBLE);

        }

    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            AccelerometerData data = new AccelerometerData();
            data.setAccelerometer_x(event.values[ACCELEROMETER_X]);
            data.setAccelerometer_y(event.values[ACCELEROMETER_Y]);
            data.setAccelerometer_z(event.values[ACCELEROMETER_Z]);
            accelerometer.add(data);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
