package jp.co.akiguchilab.healthcaremanagement.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.IBinder;

import jp.co.akiguchilab.healthcaremanagement.R;

public class CountService extends Service {
    private static final String TAG = CountService.class.getSimpleName();

    public static final String ACTION = "Walk Count Service";

    int whoami = 0;
    int pHour = 0;
    int pLong = 0;
    int sInterval = 0;
    int mInterval = 0;
    boolean useVib = false;

    CountMaster master = null;
    //DatabaseHelper databaseHelper = null;
    LocationUploader locationUploader = null;

    @Override
    public void onCreate() {
        super.onCreate();

        //databaseHelper = new DatabaseHelper(this);
        SensorManager sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        master = new CountMaster(sensorManager/*, databaseHelper*/);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        whoami = intent.getIntExtra("whoami", 0);
        sInterval = intent.getIntExtra("sInterval", 0) * 1000;
        mInterval = 10;
        pHour = intent.getIntExtra("phour", 0);
        pLong = intent.getIntExtra("plong", 24);

        locationUploader = new LocationUploader(this, whoami);

        Notification notification = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("歩数計サービス")
                .setSmallIcon(R.drawable.icon)
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, notification);

        startForeground(1, notification);

        return START_REDELIVER_INTENT;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        master.stopSensor();
        //databaseHelper.close();
    }
}
