package jp.co.akiguchilab.healthcaremanagement.training;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.StringTokenizer;

import jp.co.akiguchilab.healthcaremanagement.R;

/**
 * Created by i09324 on 2015/02/27.
 * 補強運動計測アクティビティ
 */
public class TrainingActivity extends Activity implements SensorEventListener {
    private SensorManager manager;
    private Vibrator vibrator;

    private String filepath = "";
    private float threshold_x_min = 0;
    private float threshold_x_max = 0;
    private float threshold_y_min = 0;
    private float threshold_y_max = 0;
    private float threshold_z_min = 0;
    private float threshold_z_max = 0;

    private boolean x_flag = false;
    private boolean y_flag = false;
    private boolean z_flag = false;
    private boolean x_counted = false;
    private boolean y_counted = false;
    private boolean z_counted = false;

    private TextView countView;
    private int count;
    private boolean onPractice = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_training);

        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        TextView titleView = (TextView) findViewById(R.id.training_title);
        countView = (TextView) findViewById(R.id.training_counter);

        // 運動名の取得
        titleView.setText(getIntent().getExtras().getString("title", "エラー"));

        // 運動の閾値ファイルのパスを取得
        filepath = getIntent().getExtras().getString("path", "");

        // 練習モードか？
        onPractice = getIntent().getExtras().getBoolean("onPractice");

        // ファイルから閾値を読み込み
        readThreshold(filepath);
    }

    @Override
    protected void onResume() {
        super.onResume();
        countTraining();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //manager.unregisterListener(this);
    }

    public void readThreshold(String path) {
        // ファイルから読み込む
        // 軸名,MAX,MIN
        try {
            File file = new File(path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            int count = 0;
            String line;

            while ((line = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, ",");
                st.nextToken();

                if (count == 0) {
                    threshold_x_max = Float.parseFloat(st.nextToken());
                    threshold_x_min = Float.parseFloat(st.nextToken());
                } else if (count == 1) {
                    threshold_y_max = Float.parseFloat(st.nextToken());
                    threshold_y_min = Float.parseFloat(st.nextToken());
                } else if (count == 2) {
                    threshold_z_max = Float.parseFloat(st.nextToken());
                    threshold_z_min = Float.parseFloat(st.nextToken());
                }
                count++;
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void countTraining() {
        List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            Sensor sensor = sensors.get(0);
            manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (!x_counted) {
                if (!x_flag && threshold_x_min > event.values[0]) {
                    x_flag = true;
                }
                if (x_flag && threshold_x_max < event.values[0]) {
                    x_counted = true;
                }
            }
            if (!y_counted) {
                if (!y_flag && threshold_y_min > event.values[1]) {
                    y_flag = true;
                }
                if (y_flag && threshold_y_max < event.values[1]) {
                    y_counted = true;
                }
            }
            if (!z_counted) {
                if (!z_flag && threshold_z_min > event.values[2]) {
                    z_flag = true;
                }
                if (z_flag && threshold_z_max < event.values[2]) {
                    z_counted = true;
                }
            }

            if (x_counted && y_counted && z_counted) {
                // 10ミリ秒バイブレーション
                vibrator.vibrate(10);

                // 1回カウント
                count++;
                countView.setText(count + "");

                Log.d("ResetCounted", "Counted Clear!");
                // フラグのリセット
                x_flag = false;
                y_flag = false;
                z_flag = false;
                x_counted = false;
                y_counted = false;
                z_counted = false;
            }

            // TODO: 練習モード時は計測を保存しない
            if (!onPractice) {
                // TODO: 計測回数の保存処理

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
