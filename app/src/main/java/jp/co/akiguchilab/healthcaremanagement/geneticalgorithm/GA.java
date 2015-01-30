package jp.co.akiguchilab.healthcaremanagement.geneticalgorithm;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.akiguchilab.healthcaremanagement.R;

/**
 * Created by i09324 on 2014/08/28.
 */
public class GA extends Activity implements SensorEventListener, OnClickListener {
    private static int ACCELEROMETER_X = 0;
    private static int ACCELEROMETER_Y = 1;
    private static int ACCELEROMETER_Z = 2;

    private boolean SENSOR_ON_FLAG = false;

    private SensorManager manager;
    private ArrayList<AccelerometerData> accelerometer = new ArrayList<AccelerometerData>();

    private GeneticAlgorithm GA = new GeneticAlgorithm();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.genetic_layout);

        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Button OnOff = (Button) findViewById(R.id.genetic_button);
        OnOff.setOnClickListener(this);
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
        Button OnOff = (Button) findViewById(R.id.genetic_button);

        if (!SENSOR_ON_FLAG) {
            enableSensor();
            OnOff.setText("停止");
        } else {
            disableSensor();
            OnOff.setText("開始");

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    File sdcard_path = new File(Environment.getExternalStorageDirectory().getPath() + "/HealthCare");
                    Date date = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

                    String Fs = File.separator;

                    String filePathx = sdcard_path + Fs + "GA" + dateFormat.format(date) + "x.txt";
                    String filePathy = sdcard_path + Fs + "GA" + dateFormat.format(date) + "y.txt";
                    String filePathz = sdcard_path + Fs + "GA" + dateFormat.format(date) + "z.txt";
                    if (!sdcard_path.exists()) {
                        sdcard_path.mkdir();
                    }

                    try {
                        BufferedWriter bwx = new BufferedWriter(
                                new OutputStreamWriter(
                                        new FileOutputStream(filePathx, false), "UTF-8")
                        );
                        BufferedWriter bwy = new BufferedWriter(
                                new OutputStreamWriter(
                                        new FileOutputStream(filePathy, false), "UTF-8")
                        );
                        BufferedWriter bwz = new BufferedWriter(
                                new OutputStreamWriter(
                                        new FileOutputStream(filePathz, false), "UTF-8")
                        );

                        for (int i = 0; i < accelerometer.size(); i++) {
                            bwx.write(accelerometer.get(i).getAccelerometer_x() + "");
                            bwx.newLine();
                            bwy.write(accelerometer.get(i).getAccelerometer_y() + "");
                            bwy.newLine();
                            bwz.write(accelerometer.get(i).getAccelerometer_z() + "");
                            bwz.newLine();
                        }
                        bwx.flush();
                        bwx.close();
                        bwy.flush();
                        bwy.close();
                        bwz.flush();
                        bwz.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();

            Thread GAThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    GA.start(accelerometer);
                }
            });
            GAThread.start();
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
