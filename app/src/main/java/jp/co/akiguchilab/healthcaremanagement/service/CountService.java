package jp.co.akiguchilab.healthcaremanagement.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.IBinder;

import jp.co.akiguchilab.healthcaremanagement.CountMaster;
import jp.co.akiguchilab.healthcaremanagement.DatabaseHelper;

public class CountService extends Service{
    private static final String TAG = CountService.class.getSimpleName();

    public static final String ACTION = "Walk Count Service";

    private CountMaster master;
    private DatabaseHelper databasehelper;

    public CountService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        databasehelper = new DatabaseHelper(this);
        master = new CountMaster(sensorManager, databasehelper);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        master.stopSensor();
        databasehelper.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new CountBinder(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    public long getCounter() {
        return (master == null) ? -1 : master.getCounter();
    }
}
