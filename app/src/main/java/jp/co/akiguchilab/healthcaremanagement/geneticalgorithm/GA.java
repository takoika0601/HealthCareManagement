package jp.co.akiguchilab.healthcaremanagement.geneticalgorithm;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

import java.util.ArrayList;
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
            GA.doGA(this, accelerometer);
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
