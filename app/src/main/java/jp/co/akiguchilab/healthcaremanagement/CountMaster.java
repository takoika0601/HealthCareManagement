package jp.co.akiguchilab.healthcaremanagement;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Date;
import java.util.List;

public class CountMaster implements SensorEventListener {
    private static final String TAG = CountMaster.class.getSimpleName();

    private SensorManager sensorManager;
    DatabaseHelper databasehelper;

    //前回の値を保存
    private float oldX = 0f;
    private float oldY = 0f;
    private float oldZ = 0f;

    //取得した加速度格納
    private float X = 0f;
    private float Y = 0f;
    private float Z = 0f;

    //重複してカウントしないためのフラグ
    boolean counted = true;

    //歩数をカウントする
    long counter = 0;

    //前回のベクトルの値保存
    double oldVector = 0;

    //取得したベクトル
    double vector = 0;

    //ベクトル変化を検知した時間カウント
    long changeTime = 0;

    //歩いたと判定するための閾値
    double threshold = 15;

    //軸方向転換の最小閾値
    double thresholdmin = 1;

    //ベクトル変化がない時間の閾値
    long thresholdtime = 190;

    //x軸方向に加速度が働いているか
    boolean vecX = true;
    //y軸
    boolean vecY = true;
    //z軸
    boolean vecZ = true;

    //加速度の方向が転換した回数
    int vectorchangecount = 0;

    public float getX() {
        return X;
    }

    public float getY() {
        return Y;
    }

    public float getZ() {
        return Z;
    }

    public double getVecter() {
        return vector;
    }

    public long getCounter() {
        return counter;
    }

    public CountMaster(SensorManager sensorManager, DatabaseHelper databasehelper) {
        this.databasehelper = databasehelper;
        long counter = databasehelper.selectCount();
        this.counter = counter;

        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            Sensor s = sensors.get(0);
            sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void stopSensor() {
        if (sensorManager != null) sensorManager.unregisterListener(this);
        sensorManager = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //加速度の増加量を求める
            X = event.values[0] - oldX;
            Y = event.values[1] - oldY;
            Z = event.values[2] - oldZ;

            //ベクトルを求める
            vector = (X * X + Y * Y + Z * Z);

            //各軸の加速度が閾値の最低値を超えているか
            boolean dX = Math.abs(X) > thresholdmin && vecX != (X >= 0);
            boolean dY = Math.abs(Y) > thresholdmin && vecY != (Y >= 0);
            boolean dZ = Math.abs(Z) > thresholdmin && vecZ != (Z >= 0);

            long dt = new Date().getTime() - changeTime;

            if (vector > threshold && dt > thresholdtime && (dX || dY || dZ)) {
                vectorchangecount++;
                changeTime = new Date().getTime();
            }

            if (vectorchangecount > 1 || vector < 1) {
                counted = false;
                vectorchangecount = 0;
            }

            //カウント許可されていて、閾値を超えるベクトルである場合、カウントする
            if (!counted && vector > threshold) {
                counter++;
                counted = true;
                vectorchangecount = 0;
                databasehelper.updateCount(counter);
            }

            //前回の加速度の向きを保存する
            vecX = X >= 0;
            vecY = Y >= 0;
            vecZ = Z >= 0;

            //前回のベクトルを保存する
            oldVector = vector;

            //前回の加速度を保存する
            oldX = event.values[0];
            oldY = event.values[1];
            oldZ = event.values[2];
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO 自動生成されたメソッド・スタブ

    }
}
